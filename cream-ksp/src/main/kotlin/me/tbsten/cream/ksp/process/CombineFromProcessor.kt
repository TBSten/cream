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
import me.tbsten.cream.ksp.CreamSymbolProcessor
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.transform.appendCombineToFunction
import me.tbsten.cream.ksp.transform.appendCopyFunction
import me.tbsten.cream.ksp.util.annotationsOf
import me.tbsten.cream.ksp.util.classListArgument
import me.tbsten.cream.ksp.util.createNewKotlinFile
import me.tbsten.cream.ksp.util.extractKDoc
import me.tbsten.cream.ksp.util.extractPropertyMappings
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
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

        val sourceClasses =
            combineFromAnnotations
                .classListArgument("sources")
                .map { it.declaration }
                .map { declaration ->
                    declaration.requireClassDeclaration(
                        annotationName = CombineFrom::class.simpleName!!,
                        context = "Specified in @${CombineFrom::class.simpleName}.sources of ${target.fullName}",
                    )
                }.toList()

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

        codeGenerator
            .createNewKotlinFile(
                dependencies = Dependencies(aggregating = true, targetDeclaration.containingFile!!),
                packageName = targetClass.packageName,
                fileName = "CombineFrom__${primarySource.underPackageName}__${targetClass.underPackageName}",
            ) {
                it.appendLine("import me.tbsten.cream.*")
                it.appendLine()

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
                )
            }
    }

    return invalidCombineFromTargets
}
