package me.tbsten.cream.ksp.feature.copyTo

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.validate
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.classListArgument
import me.tbsten.cream.ksp.core.common.copyVisibilityArgument
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.extractKDoc
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.funNameTemplate
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.requireFunNameSupportsFanout
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.core.common.warnIfSourceExcludeHasNoEffect
import me.tbsten.cream.ksp.core.copyFun.appendCopyFunction
import me.tbsten.cream.ksp.util.ksp.isSealed

context(processContext: ProcessContext)
internal fun processCopyTo(): List<KSAnnotated> {
    val (copyToTargets, invalidCopyToTargets) =
        processContext.resolver
            .getSymbolsWithAnnotation(
                annotationName = CopyTo::class.fullName,
            ).partition { it.validate() }

    copyToTargets.forEach { target ->
        val sourceDeclaration =
            target as? KSDeclaration
                ?: run {
                    processContext.logger.reportCreamError(
                        InvalidCreamUsageException(
                            message = "@${CopyTo::class.simpleName} must be applied to a class, interface, or typealias.",
                            solution = "Please apply @${CopyTo::class.simpleName} to `class`, `interface`, or `typealias`",
                        ),
                        target,
                    )
                    return@forEach
                }
        val sourceClass =
            sourceDeclaration.resolveClassDeclarationOrReport(
                annotationName = CopyTo::class.simpleName!!,
                logger = processContext.logger,
            ) ?: return@forEach

        val copyToAnnotations = target.annotationsOf(CopyTo::class)

        // CopyTo.targets: List<KClass<*>>. If any target cannot be resolved to a class, an error has
        // been reported and we skip this source so no partial file is emitted.
        val resolvedTargets =
            copyToAnnotations
                .classListArgument("targets")
                .map { it.declaration }
                .map { declaration ->
                    declaration.resolveClassDeclarationOrReport(
                        annotationName = CopyTo::class.simpleName!!,
                        logger = processContext.logger,
                        context = "Specified in @${CopyTo::class.simpleName}.targets of ${target.fullName}",
                        ksNode = sourceDeclaration,
                    )
                }.toList()
        if (resolvedTargets.any { it == null }) return@forEach
        val targetClasses = resolvedTargets.filterNotNull()

        val (kdocDescription, kdocExamples) =
            copyToAnnotations.firstOrNull()?.extractKDoc() ?: ("" to emptyList())

        val visibility =
            copyToAnnotations.firstOrNull()?.copyVisibilityArgument() ?: CopyVisibility.INHERIT

        val funNameTemplate =
            copyToAnnotations.firstOrNull()?.funNameTemplate() ?: DefaultCopyFunctionName

        val funNameOk =
            requireFunNameSupportsFanout(
                funNameTemplate = funNameTemplate,
                generatesMultipleFunctions = targetClasses.size > 1 || targetClasses.any { it.isSealed() },
                annotationSimpleName = CopyTo::class.simpleName!!,
                declarationFullName = sourceClass.fullName,
                logger = processContext.logger,
                ksNode = sourceDeclaration,
            )
        if (!funNameOk) return@forEach

        val gsa =
            GenerateSourceAnnotation.CopyTo(
                annotationTarget = sourceDeclaration,
                kdocDescription = kdocDescription,
                kdocExamples = kdocExamples,
            )

        // Warn for @CopyTo.Exclude on source properties that match no target parameter (no-op).
        // Sealed interface targets have no primary constructor; expand them to their concrete
        // subclasses to mirror the fan-out done by appendCopyToSealedClassFunction.
        val allTargetParams: List<KSValueParameter> =
            targetClasses.flatMap { target ->
                if (target.isSealed()) {
                    target
                        .getSealedSubclasses()
                        .flatMap { it.primaryConstructor?.parameters.orEmpty() }
                        .toList()
                } else {
                    target.primaryConstructor?.parameters.orEmpty()
                }
            }
        sourceClass.getAllProperties().forEach { prop ->
            prop.warnIfSourceExcludeHasNoEffect(allTargetParams, sourceClass, gsa, processContext.logger)
        }

        processContext.codeGenerator
            .createNewKotlinFile(
                dependencies = Dependencies(aggregating = true, sourceDeclaration.containingFile!!),
                packageName = sourceClass.packageName,
                fileName = "CopyTo__${sourceClass.underPackageName}",
            ) {
                targetClasses.forEach { targetClass ->
                    // generate sourceClass to sourceClass copy function
                    with(processContext.options) {
                        with(processContext.logger) {
                            it.appendCopyFunction(
                                source = sourceClass,
                                target = targetClass,
                                omitPackages = listOf("kotlin", sourceClass.packageName.asString()),
                                generateSourceAnnotation = gsa,
                                notCopyToObject = false,
                                visibility = visibility,
                                funNameTemplate = funNameTemplate,
                            )
                        }
                    }
                }
            }
    }
    return invalidCopyToTargets
}
