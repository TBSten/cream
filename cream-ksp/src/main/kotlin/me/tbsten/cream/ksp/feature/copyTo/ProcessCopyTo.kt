package me.tbsten.cream.ksp.feature.copyTo

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.validate
import me.tbsten.cream.CopyTo
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.asDeclarationOrReport
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.onInvalid
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.resolveClassListOrReport
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.core.common.validateFunName
import me.tbsten.cream.ksp.core.common.warnIfSourceExcludeHasNoEffect
import me.tbsten.cream.ksp.core.copyFun.appendCopyFunction
import me.tbsten.cream.ksp.util.ksp.isSealed
import me.tbsten.cream.ksp.util.with

private val annotationName = CopyTo::class.simpleName!!

context(processContext: ProcessContext)
internal fun processCopyTo(): List<KSAnnotated> =
    with(processContext.logger, processContext.options) {
        val (copyToTargets, invalidCopyToTargets) =
            processContext.resolver
                .getSymbolsWithAnnotation(
                    annotationName = CopyTo::class.fullName,
                ).partition { it.validate() }

        copyToTargets.forEach { target ->
            val sourceDeclaration = target.asDeclarationOrReport(annotationName) ?: return@forEach
            val sourceClass =
                sourceDeclaration.resolveClassDeclarationOrReport(
                    annotationName = annotationName,
                    logger = processContext.logger,
                ) ?: return@forEach

            val copyToAnnotations = target.annotationsOf(CopyTo::class)

            // CopyTo.targets: List<KClass<*>>. If any target cannot be resolved to a class, an error has
            // been reported and we skip this source so no partial file is emitted.
            val targetClasses =
                copyToAnnotations.resolveClassListOrReport("targets", annotationName, sourceDeclaration)
                    ?: return@forEach

            val copyToAnnotation =
                copyToAnnotations.firstOrNull() ?: return@forEach

            val generateSourceAnnotation =
                GenerateSourceAnnotation.CopyTo(annotation = copyToAnnotation).also { gsa ->
                    gsa
                        .validateFunName(
                            generatesMultipleFunctions = targetClasses.size > 1 || targetClasses.any { it.isSealed() },
                            declarationFullName = sourceClass.fullName,
                            ksNode = sourceDeclaration,
                        ).onInvalid { return@forEach }
                }

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
                prop.warnIfSourceExcludeHasNoEffect(allTargetParams, sourceClass, generateSourceAnnotation, processContext.logger)
            }

            processContext.codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceDeclaration.containingFile!!),
                    packageName = sourceClass.packageName,
                    fileName = "CopyTo__${sourceClass.underPackageName}",
                ) {
                    targetClasses.forEach { targetClass ->
                        // generate sourceClass to sourceClass copy function
                        it.appendCopyFunction(
                            source = sourceClass,
                            target = targetClass,
                            omitPackages = listOf("kotlin", sourceClass.packageName.asString()),
                            generateSourceAnnotation = generateSourceAnnotation,
                        )
                    }
                }
        }
        return invalidCopyToTargets
    }
