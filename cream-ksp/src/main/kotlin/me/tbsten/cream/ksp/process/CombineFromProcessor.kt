package me.tbsten.cream.ksp.process

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.CreamSymbolProcessor
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.transform.appendCombineToFunction
import me.tbsten.cream.ksp.transform.appendCopyFunction
import me.tbsten.cream.ksp.transform.resolveFunName
import me.tbsten.cream.ksp.util.annotationsOf
import me.tbsten.cream.ksp.util.classListArgument
import me.tbsten.cream.ksp.util.copyVisibilityArgument
import me.tbsten.cream.ksp.util.createNewKotlinFile
import me.tbsten.cream.ksp.util.extractKDoc
import me.tbsten.cream.ksp.util.extractPropertyMappings
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.funNameTemplate
import me.tbsten.cream.ksp.util.isSealed
import me.tbsten.cream.ksp.util.lines
import me.tbsten.cream.ksp.util.requireClassDeclaration
import me.tbsten.cream.ksp.util.resolveToClassDeclaration
import me.tbsten.cream.ksp.util.underPackageName

internal fun CreamSymbolProcessor.processCombineFrom(resolver: Resolver): List<KSAnnotated> {
    val (combineFromTargets, invalidCombineFromTargets) =
        resolver
            .getSymbolsWithAnnotation(
                annotationName = CombineFrom::class.fullName,
            ).partition { it.validate() }

    combineFromTargets.forEach { target ->
        val targetDeclaration =
            target as? KSDeclaration
                ?: throw InvalidCreamUsageException(
                    message = "@${CombineFrom::class.simpleName} must be applied to a class, interface, or typealias.",
                    solution = "Please apply @${CombineFrom::class.simpleName} to `class`, `interface`, or `typealias`",
                )
        val targetClass = targetDeclaration.requireClassDeclaration(annotationName = CombineFrom::class.simpleName!!)

        val combineFromAnnotations = target.annotationsOf(CombineFrom::class)

        // @CombineFrom is @Repeatable; the sources of every occurrence are flattened into one
        // merged copy function. Stacking it twice with the same source set (or simply repeating a
        // class across occurrences) is an idempotent re-declaration, so dedupe the collected
        // sources: keeping a duplicate would emit a function with two identically named parameters
        // ("Conflicting declarations") and re-list the same source in KDoc (issue #101).
        val sourceClasses =
            combineFromAnnotations
                .classListArgument("sources")
                .map { it.declaration }
                .map { declaration ->
                    declaration.requireClassDeclaration(
                        annotationName = CombineFrom::class.simpleName!!,
                        context = "Specified in @${CombineFrom::class.simpleName}.sources of ${target.fullName}",
                    )
                }.distinct()
                .toList()

        // Need at least one source class
        if (sourceClasses.isEmpty()) {
            throw InvalidCreamUsageException(
                message = "@${CombineFrom::class.simpleName} requires at least one source class.",
                solution = "Specify at least one source class in @${CombineFrom::class.simpleName}.sources of ${target.fullName}.",
            )
        }

        // First source class is the primary source (extension function receiver)
        val primarySource = sourceClasses.first()
        val otherSources = sourceClasses.drop(1)

        val (kdocDescription, kdocExamples) =
            combineFromAnnotations.firstOrNull()?.extractKDoc() ?: ("" to emptyList())

        val visibility =
            combineFromAnnotations.firstOrNull()?.copyVisibilityArgument() ?: CopyVisibility.INHERIT

        // @CombineFrom is @Repeatable and all occurrences are merged into ONE generated function,
        // so the funName must be unambiguous. Reading it from only the first occurrence would
        // silently drop a different funName set on a later one — instead, require the explicit
        // funName values to agree.
        val explicitFunNameTemplates =
            combineFromAnnotations
                .map { it.funNameTemplate() }
                .filter { it != DefaultCopyFunctionName }
                .distinct()
                .toList()
        // Compare the *resolved* names, not the raw (KSP-folded) templates: the occurrences are
        // merged into one function that takes a single name, so they are only ambiguous when they
        // resolve to different names. Resolving also keeps the diagnostic readable — it shows
        // "toFoo" rather than the internal "to{{cream:CopyTargetSimpleName}}" placeholder form.
        val explicitFunNames =
            explicitFunNameTemplates
                .map { resolveFunName(it, primarySource, targetClass, options) }
                .distinct()
        if (explicitFunNames.size > 1) {
            throw InvalidCreamUsageException(
                message =
                    lines(
                        "@${CombineFrom::class.simpleName} on ${targetClass.fullName} is repeated with conflicting funName values:",
                        explicitFunNames.joinToString(", ") { "\"$it\"" },
                        "Stacked @${CombineFrom::class.simpleName} annotations are merged into a single generated function, so funName must be unambiguous.",
                    ),
                solution =
                    lines(
                        "Set the same funName on every @${CombineFrom::class.simpleName} of ${targetClass.fullName}, or set it on only one.",
                    ),
            )
        }
        val funNameTemplate = explicitFunNameTemplates.firstOrNull() ?: DefaultCopyFunctionName

        codeGenerator
            .createNewKotlinFile(
                dependencies = Dependencies(aggregating = true, targetDeclaration.containingFile!!),
                packageName = targetClass.packageName,
                fileName = "CombineFrom__${primarySource.underPackageName}__${targetClass.underPackageName}",
            ) {
                // Generate combine function with multiple sources
                it.appendCombineToFunction(
                    primarySource = primarySource,
                    otherSources = otherSources,
                    target = targetClass,
                    options = options,
                    omitPackages = listOf("kotlin", primarySource.packageName.asString()),
                    generateSourceAnnotation =
                        GenerateSourceAnnotation.CombineFrom(
                            annotationTarget = targetDeclaration,
                            kdocDescription = kdocDescription,
                            kdocExamples = kdocExamples,
                        ),
                    visibility = visibility,
                    funNameTemplate = funNameTemplate,
                    logger = logger,
                )
            }
    }

    return invalidCombineFromTargets
}
