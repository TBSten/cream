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
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.CreamSymbolProcessor
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.transform.appendCombineToFunction
import me.tbsten.cream.ksp.transform.appendCopyFunction
import me.tbsten.cream.ksp.transform.requireFunNameSupportsFanout
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

internal fun CreamSymbolProcessor.processCopyFrom(resolver: Resolver): List<KSAnnotated> {
    val (copyFromTargets, invalidCopyFromTargets) =
        resolver
            .getSymbolsWithAnnotation(
                annotationName = CopyFrom::class.fullName,
            ).partition { it.validate() }

    copyFromTargets.forEach { target ->
        val targetDeclaration =
            target as? KSDeclaration
                ?: throw InvalidCreamUsageException(
                    message = "@${CopyFrom::class.simpleName} must be applied to a class, interface, or typealias.",
                    solution = "Please apply @${CopyFrom::class.simpleName} to `class`, `interface`, or `typealias`",
                )
        val targetClass = targetDeclaration.requireClassDeclaration(annotationName = CopyFrom::class.simpleName!!)

        val copyFromAnnotations = target.annotationsOf(CopyFrom::class)

        // CopyFrom.sources: List<KClass<*>>
        val sourceClasses =
            copyFromAnnotations
                .classListArgument("sources")
                .map { it.declaration }
                .map { declaration ->
                    declaration.requireClassDeclaration(
                        annotationName = CopyFrom::class.simpleName!!,
                        context = "Specified in @${CopyFrom::class.simpleName}.sources of ${target.fullName}",
                    )
                }.toList()

        val (kdocDescription, kdocExamples) =
            copyFromAnnotations.firstOrNull()?.extractKDoc() ?: ("" to emptyList())

        val visibility =
            copyFromAnnotations.firstOrNull()?.copyVisibilityArgument() ?: CopyVisibility.INHERIT

        val funNameTemplate =
            copyFromAnnotations.firstOrNull()?.funNameTemplate() ?: DefaultCopyFunctionName

        requireFunNameSupportsFanout(
            funNameTemplate = funNameTemplate,
            generatesMultipleFunctions = sourceClasses.size > 1 || targetClass.isSealed(),
            annotationSimpleName = CopyFrom::class.simpleName!!,
            declarationFullName = targetClass.fullName,
        )

        codeGenerator
            .createNewKotlinFile(
                dependencies = Dependencies(aggregating = true, targetDeclaration.containingFile!!),
                packageName = targetClass.packageName,
                fileName = "CopyFrom__${targetClass.underPackageName}",
            ) {
                it.appendLine("import me.tbsten.cream.*")
                it.appendLine()

                sourceClasses.forEach { sourceClass ->
                    it.appendCopyFunction(
                        source = sourceClass,
                        target = targetClass,
                        options = options,
                        omitPackages = listOf("kotlin", targetClass.packageName.asString()),
                        generateSourceAnnotation =
                            GenerateSourceAnnotation.CopyFrom(
                                annotationTarget = targetDeclaration,
                                kdocDescription = kdocDescription,
                                kdocExamples = kdocExamples,
                            ),
                        notCopyToObject = false,
                        visibility = visibility,
                        funNameTemplate = funNameTemplate,
                        logger = logger,
                    )
                }
            }
    }

    return invalidCopyFromTargets
}
