package me.tbsten.cream.ksp.feature.copyFrom

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import me.tbsten.cream.CopyFrom
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
import me.tbsten.cream.ksp.core.copyFun.appendCopyFunction
import me.tbsten.cream.ksp.util.ksp.isSealed
import me.tbsten.cream.ksp.util.with

private val annotationName = CopyFrom::class.simpleName!!

context(processContext: ProcessContext)
internal fun processCopyFrom(): List<KSAnnotated> =
    with(processContext.logger, processContext.options) {
        val (copyFromTargets, invalidCopyFromTargets) =
            processContext.resolver
                .getSymbolsWithAnnotation(
                    annotationName = CopyFrom::class.fullName,
                ).partition { it.validate() }

        copyFromTargets.forEach { target ->
            val targetDeclaration =
                target.asDeclarationOrReport(annotationName) ?: return@forEach
            val targetClass =
                targetDeclaration.resolveClassDeclarationOrReport(
                    annotationName = annotationName,
                    logger = processContext.logger,
                ) ?: return@forEach

            val copyFromAnnotations = target.annotationsOf(CopyFrom::class)

            // CopyFrom.sources: List<KClass<*>>. If any source cannot be resolved to a class, an error
            // has been reported and we skip this target so no partial file is emitted.
            val sourceClasses =
                copyFromAnnotations.resolveClassListOrReport("sources", annotationName, targetDeclaration) ?: return@forEach

            val copyFromAnnotation =
                copyFromAnnotations.firstOrNull() ?: return@forEach

            val generateSourceAnnotation =
                GenerateSourceAnnotation.CopyFrom(annotation = copyFromAnnotation).also { gsa ->
                    gsa
                        .validateFunName(
                            generatesMultipleFunctions = sourceClasses.size > 1 || targetClass.isSealed(),
                            declarationFullName = targetClass.fullName,
                            ksNode = targetDeclaration,
                        ).onInvalid { return@forEach }
                }

            processContext.codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, targetDeclaration.containingFile!!),
                    packageName = targetClass.packageName,
                    fileName = "CopyFrom__${targetClass.underPackageName}",
                ) {
                    sourceClasses.forEach { sourceClass ->
                        it.appendCopyFunction(
                            source = sourceClass,
                            target = targetClass,
                            omitPackages = listOf("kotlin", targetClass.packageName.asString()),
                            generateSourceAnnotation = generateSourceAnnotation,
                            annotated = targetDeclaration,
                        )
                    }
                }
        }

        return invalidCopyFromTargets
    }
