package me.tbsten.cream.ksp.feature.copyFrom

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.validate
import me.tbsten.cream.CopyFrom
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
import me.tbsten.cream.ksp.core.copyFun.appendCopyFunction
import me.tbsten.cream.ksp.util.ksp.isSealed

context(processContext: ProcessContext)
internal fun processCopyFrom(): List<KSAnnotated> {
    val (copyFromTargets, invalidCopyFromTargets) =
        processContext.resolver
            .getSymbolsWithAnnotation(
                annotationName = CopyFrom::class.fullName,
            ).partition { it.validate() }

    copyFromTargets.forEach { target ->
        val targetDeclaration =
            target as? KSDeclaration
                ?: run {
                    processContext.logger.reportCreamError(
                        InvalidCreamUsageException(
                            message = "@${CopyFrom::class.simpleName} must be applied to a class, interface, or typealias.",
                            solution = "Please apply @${CopyFrom::class.simpleName} to `class`, `interface`, or `typealias`",
                        ),
                        target,
                    )
                    return@forEach
                }
        val targetClass =
            targetDeclaration.resolveClassDeclarationOrReport(
                annotationName = CopyFrom::class.simpleName!!,
                logger = processContext.logger,
            ) ?: return@forEach

        val copyFromAnnotations = target.annotationsOf(CopyFrom::class)

        // CopyFrom.sources: List<KClass<*>>. If any source cannot be resolved to a class, an error
        // has been reported and we skip this target so no partial file is emitted.
        val resolvedSources =
            copyFromAnnotations
                .classListArgument("sources")
                .map { it.declaration }
                .map { declaration ->
                    declaration.resolveClassDeclarationOrReport(
                        annotationName = CopyFrom::class.simpleName!!,
                        logger = processContext.logger,
                        context = "Specified in @${CopyFrom::class.simpleName}.sources of ${target.fullName}",
                        ksNode = targetDeclaration,
                    )
                }.toList()
        if (resolvedSources.any { it == null }) return@forEach
        val sourceClasses = resolvedSources.filterNotNull()

        val (kdocDescription, kdocExamples) =
            copyFromAnnotations.firstOrNull()?.extractKDoc() ?: ("" to emptyList())

        val visibility =
            copyFromAnnotations.firstOrNull()?.copyVisibilityArgument() ?: CopyVisibility.INHERIT

        val funNameTemplate =
            copyFromAnnotations.firstOrNull()?.funNameTemplate() ?: DefaultCopyFunctionName

        val funNameOk =
            requireFunNameSupportsFanout(
                funNameTemplate = funNameTemplate,
                generatesMultipleFunctions = sourceClasses.size > 1 || targetClass.isSealed(),
                annotationSimpleName = CopyFrom::class.simpleName!!,
                declarationFullName = targetClass.fullName,
                logger = processContext.logger,
                ksNode = targetDeclaration,
            )
        if (!funNameOk) return@forEach

        processContext.codeGenerator
            .createNewKotlinFile(
                dependencies = Dependencies(aggregating = true, targetDeclaration.containingFile!!),
                packageName = targetClass.packageName,
                fileName = "CopyFrom__${targetClass.underPackageName}",
            ) {
                sourceClasses.forEach { sourceClass ->
                    with(processContext.options) {
                        with(processContext.logger) {
                            it.appendCopyFunction(
                                source = sourceClass,
                                target = targetClass,
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
                            )
                        }
                    }
                }
            }
    }

    return invalidCopyFromTargets
}
