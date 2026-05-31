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
import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.CreamSymbolProcessor
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.transform.appendCombineToFunction
import me.tbsten.cream.ksp.transform.appendCopyFunction
import me.tbsten.cream.ksp.transform.requireFunNameSupportsFanout
import me.tbsten.cream.ksp.transform.warnIfSourceExcludeHasNoEffect
import me.tbsten.cream.ksp.util.annotationsOf
import me.tbsten.cream.ksp.util.classListArgument
import me.tbsten.cream.ksp.util.copyVisibilityArgument
import me.tbsten.cream.ksp.util.createNewKotlinFile
import me.tbsten.cream.ksp.util.extractKDoc
import me.tbsten.cream.ksp.util.extractPropertyMappings
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.funNameTemplate
import me.tbsten.cream.ksp.util.isSealed
import me.tbsten.cream.ksp.util.requireClassDeclaration
import me.tbsten.cream.ksp.util.resolveToClassDeclaration
import me.tbsten.cream.ksp.util.underPackageName

private data class CombineToSourceEntry(
    val sourceDeclaration: KSDeclaration,
    val kdocDescription: String,
    val kdocExamples: List<String>,
    val visibility: CopyVisibility,
    val funNameTemplate: String,
)

private class TargetSourcesMapForCombineTo : MutableMap<KSClassDeclaration, MutableList<CombineToSourceEntry>> by mutableMapOf() {
    fun put(
        targetClass: KSClassDeclaration,
        entry: CombineToSourceEntry,
    ) {
        getOrPut(targetClass) { mutableListOf() }.add(entry)
    }
}

internal fun CreamSymbolProcessor.processCombineTo(resolver: Resolver): List<KSAnnotated> {
    val (combineToTargets, invalidCombineToTargets) =
        resolver
            .getSymbolsWithAnnotation(
                annotationName = CombineTo::class.fullName,
            ).partition { it.validate() }

    // Group source classes by their target classes
    val targetToSourcesMap = TargetSourcesMapForCombineTo()

    combineToTargets.forEach { target ->
        val sourceDeclaration =
            target as? KSDeclaration
                ?: throw InvalidCreamUsageException(
                    message = "@${CombineTo::class.simpleName} must be applied to a class, interface, or typealias.",
                    solution = "Please apply @${CombineTo::class.simpleName} to `class`, `interface`, or `typealias`",
                )
        val sourceClass = sourceDeclaration.requireClassDeclaration(annotationName = CombineTo::class.simpleName!!)

        val combineToAnnotations = target.annotationsOf(CombineTo::class)

        // CombineTo.targets: List<KClass<*>>
        val targetClasses =
            combineToAnnotations
                .classListArgument("targets")
                .map { it.declaration }
                .map { declaration ->
                    declaration.requireClassDeclaration(
                        annotationName = CombineTo::class.simpleName!!,
                        context = "Specified in @${CombineTo::class.simpleName}.targets of ${target.fullName}",
                    )
                }.toList()

        val (kdocDescription, kdocExamples) =
            combineToAnnotations.firstOrNull()?.extractKDoc() ?: ("" to emptyList())

        val visibility =
            combineToAnnotations.firstOrNull()?.copyVisibilityArgument() ?: CopyVisibility.INHERIT

        val funNameTemplate =
            combineToAnnotations.firstOrNull()?.funNameTemplate() ?: DefaultCopyFunctionName

        requireFunNameSupportsFanout(
            funNameTemplate = funNameTemplate,
            generatesMultipleFunctions = targetClasses.size > 1,
            annotationSimpleName = CombineTo::class.simpleName!!,
            declarationFullName = sourceClass.fullName,
        )

        // Warn ONCE per source for @CombineTo.Exclude properties that match no parameter in ANY of
        // this source's target classes. Computed here (per source, union of all targets) rather than
        // inside the per-(target, source) generation loop below — doing it there duplicated the
        // warning once per target and falsely warned for a property matched in a sibling target.
        val gsaForWarning =
            GenerateSourceAnnotation.CombineTo(
                annotationTarget = sourceDeclaration,
                kdocDescription = kdocDescription,
                kdocExamples = kdocExamples,
            )
        val allTargetParams = targetClasses.flatMap { it.primaryConstructor?.parameters.orEmpty() }
        sourceClass.getAllProperties().forEach { prop ->
            prop.warnIfSourceExcludeHasNoEffect(allTargetParams, sourceClass, gsaForWarning, logger)
        }

        // Group source classes by target class
        targetClasses.forEach { targetClass ->
            targetToSourcesMap.put(
                targetClass,
                CombineToSourceEntry(
                    sourceDeclaration = sourceDeclaration,
                    kdocDescription = kdocDescription,
                    kdocExamples = kdocExamples,
                    visibility = visibility,
                    funNameTemplate = funNameTemplate,
                ),
            )
        }
    }

    // For each target class, generate copy functions for each source class
    targetToSourcesMap.forEach { (targetClass, sourceEntries) ->
        sourceEntries.forEach { sourceEntry ->
            val sourceDeclaration = sourceEntry.sourceDeclaration
            val sourceClass = sourceDeclaration.resolveToClassDeclaration()!!
            val otherSourceClasses =
                sourceEntries
                    .filter { it.sourceDeclaration != sourceDeclaration }
                    .map { it.sourceDeclaration.resolveToClassDeclaration()!! }

            val gsa =
                GenerateSourceAnnotation.CombineTo(
                    annotationTarget = sourceDeclaration,
                    kdocDescription = sourceEntry.kdocDescription,
                    kdocExamples = sourceEntry.kdocExamples,
                )

            codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceDeclaration.containingFile!!),
                    packageName = sourceClass.packageName,
                    fileName = "CombineTo__${sourceClass.underPackageName}__${targetClass.underPackageName}",
                ) {
                    it.appendLine("import me.tbsten.cream.*")
                    it.appendLine()

                    // Generate combine function with multiple sources
                    it.appendCombineToFunction(
                        primarySource = sourceClass,
                        otherSources = otherSourceClasses,
                        target = targetClass,
                        options = options,
                        omitPackages = listOf("kotlin", sourceClass.packageName.asString()),
                        generateSourceAnnotation = gsa,
                        visibility = sourceEntry.visibility,
                        funNameTemplate = sourceEntry.funNameTemplate,
                        logger = logger,
                    )
                }
        }
    }

    return invalidCombineToTargets
}
