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
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.CreamSymbolProcessor
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.transform.appendCombineToFunction
import me.tbsten.cream.ksp.transform.appendCopyFunction
import me.tbsten.cream.ksp.util.annotationsOf
import me.tbsten.cream.ksp.util.classListArgument
import me.tbsten.cream.ksp.util.copyVisibilityArgument
import me.tbsten.cream.ksp.util.createNewKotlinFile
import me.tbsten.cream.ksp.util.extractKDoc
import me.tbsten.cream.ksp.util.extractPropertyMappings
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
import me.tbsten.cream.ksp.util.requireClassDeclaration
import me.tbsten.cream.ksp.util.resolveToClassDeclaration
import me.tbsten.cream.ksp.util.underPackageName

internal fun CreamSymbolProcessor.processCopyTo(resolver: Resolver): List<KSAnnotated> {
    val (copyToTargets, invalidCopyToTargets) =
        resolver
            .getSymbolsWithAnnotation(
                annotationName = CopyTo::class.fullName,
            ).partition { it.validate() }

    copyToTargets.forEach { target ->
        val sourceDeclaration =
            target as? KSDeclaration
                ?: throw InvalidCreamUsageException(
                    message = "@${CopyTo::class.simpleName} must be applied to a class, interface, or typealias.",
                    solution = "Please apply @${CopyTo::class.simpleName} to `class`, `interface`, or `typealias`",
                )
        val sourceClass = sourceDeclaration.requireClassDeclaration(annotationName = CopyTo::class.simpleName!!)

        val copyToAnnotations = target.annotationsOf(CopyTo::class)

        // CopyTo.targets: List<KClass<*>>
        val targetClasses =
            copyToAnnotations
                .classListArgument("targets")
                .map { it.declaration }
                .map { declaration ->
                    declaration.requireClassDeclaration(
                        annotationName = CopyTo::class.simpleName!!,
                        context = "Specified in @${CopyTo::class.simpleName}.targets of ${target.fullName}",
                    )
                }

        val (kdocDescription, kdocExamples) =
            copyToAnnotations.firstOrNull()?.extractKDoc() ?: ("" to emptyList())

        val visibility =
            copyToAnnotations.firstOrNull()?.copyVisibilityArgument() ?: CopyVisibility.INHERIT

        codeGenerator
            .createNewKotlinFile(
                dependencies = Dependencies(aggregating = true, sourceDeclaration.containingFile!!),
                packageName = sourceClass.packageName,
                fileName = "CopyTo__${sourceClass.underPackageName}",
            ) {
                it.appendLine("import me.tbsten.cream.*")
                it.appendLine()

                targetClasses.forEach { targetClass ->
                    // generate sourceClass to sourceClass copy function
                    it.appendCopyFunction(
                        source = sourceClass,
                        target = targetClass,
                        options = options,
                        omitPackages = listOf("kotlin", sourceClass.packageName.asString()),
                        generateSourceAnnotation =
                            GenerateSourceAnnotation.CopyTo(
                                annotationTarget = sourceDeclaration,
                                kdocDescription = kdocDescription,
                                kdocExamples = kdocExamples,
                            ),
                        notCopyToObject = false,
                        visibility = visibility,
                    )
                }
            }
    }
    return invalidCopyToTargets
}
