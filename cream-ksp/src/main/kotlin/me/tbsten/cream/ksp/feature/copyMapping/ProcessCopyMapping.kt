package me.tbsten.cream.ksp.feature.copyMapping

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.CopyMapping
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.asClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.funNameTemplate
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.requireFunNameSupportsFanout
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.core.copyFun.appendCopyFunction
import me.tbsten.cream.ksp.util.ksp.isSealed
import me.tbsten.cream.ksp.util.with

/**
 * Data class to hold CopyMapping annotation information
 *
 * @property sourceClass The source class for the mapping
 * @property targetClass The target class for the mapping
 * @property canReverse Whether bidirectional mapping is enabled
 * @property funNameTemplate Function-name template (used for the fan-out guard)
 * @property rawAnnotation Raw annotation this mapping was parsed from; GSA derives KDoc / funName /
 *   property mappings from it.
 */
private data class CopyMappingInfo(
    val sourceClass: KSClassDeclaration,
    val targetClass: KSClassDeclaration,
    val canReverse: Boolean,
    val funNameTemplate: String,
    val rawAnnotation: KSAnnotation,
)

/**
 * Parse one `@CopyMapping` annotation into a [CopyMappingInfo]. On any user-misuse error (missing
 * `source` / `target`, or a `source` / `target` that cannot be resolved to a class), reports a
 * clean positioned `COMPILATION_ERROR` via [logger] (anchored at [annotatedDeclaration]) and
 * returns `null` so the caller can skip the whole declaration without crashing KSP.
 */
private fun parseCopyMapping(
    annotation: KSAnnotation,
    annotatedDeclaration: KSClassDeclaration,
    logger: KSPLogger,
): CopyMappingInfo? {
    val sourceType =
        annotation.arguments
            .firstOrNull { it.name?.asString() == "source" }
            ?.value as? KSType
            ?: run {
                logger.reportCopyMappingMissingSource(annotatedDeclaration)
                return null
            }

    val targetType =
        annotation.arguments
            .firstOrNull { it.name?.asString() == "target" }
            ?.value as? KSType
            ?: run {
                logger.reportCopyMappingMissingTarget(annotatedDeclaration)
                return null
            }

    val canReverse =
        annotation.arguments
            .firstOrNull { it.name?.asString() == "canReverse" }
            ?.value as? Boolean
            ?: false

    val sourceClass =
        sourceType.declaration.resolveClassDeclarationOrReport(
            annotationName = CopyMapping::class.simpleName!!,
            logger = logger,
            context = "Specified in @${CopyMapping::class.simpleName}.source",
            ksNode = annotatedDeclaration,
        ) ?: return null

    val targetClass =
        targetType.declaration.resolveClassDeclarationOrReport(
            annotationName = CopyMapping::class.simpleName!!,
            logger = logger,
            context = "Specified in @${CopyMapping::class.simpleName}.target",
            ksNode = annotatedDeclaration,
        ) ?: return null

    return CopyMappingInfo(
        sourceClass = sourceClass,
        targetClass = targetClass,
        canReverse = canReverse,
        funNameTemplate = annotation.funNameTemplate(),
        rawAnnotation = annotation,
    )
}

context(processContext: ProcessContext)
internal fun processCopyMapping(): List<KSAnnotated> {
    val (copyMappingTargets, invalidCopyMappingTargets) =
        processContext.resolver
            .getSymbolsWithAnnotation(
                annotationName = CopyMapping::class.fullName,
            ).partition { it.validate() }

    copyMappingTargets.forEach { target ->
        val annotatedDeclaration =
            with(processContext.logger) { target.asClassDeclarationOrReport(CopyMapping::class.simpleName!!) } ?: return@forEach

        // Extract all CopyMapping annotations from the target. A malformed annotation reports a
        // clean diagnostic and yields null; skip the whole declaration so no partial file is emitted.
        val rawAnnotations = target.annotationsOf(CopyMapping::class).toList()
        val parsedMappings =
            rawAnnotations.map { rawAnnotation ->
                parseCopyMapping(rawAnnotation, annotatedDeclaration, processContext.logger)
            }
        if (parsedMappings.any { it == null }) return@forEach
        val copyMappings = parsedMappings.filterNotNull()

        // Fail fast (before opening any output file) when a plain-literal funName would
        // fan out to multiple colliding names. Mirrors CopyTo/CopyFrom, which guard before
        // createNewKotlinFile so a rejected funName never leaves a partial generated file.
        val allFunNamesOk =
            copyMappings.all { mapping ->
                requireFunNameSupportsFanout(
                    funNameTemplate = mapping.funNameTemplate,
                    generatesMultipleFunctions = mapping.canReverse || mapping.targetClass.isSealed(),
                    annotationSimpleName = CopyMapping::class.simpleName!!,
                    declarationFullName = annotatedDeclaration.fullName,
                    logger = processContext.logger,
                    ksNode = annotatedDeclaration,
                )
            }
        if (!allFunNamesOk) return@forEach

        // Group by source class to create one file per source package
        copyMappings
            .groupBy { it.sourceClass.packageName }
            .forEach { (packageName, mappings) ->
                // Use the first source class's file for dependencies
                val sourceFile =
                    mappings.firstOrNull()?.sourceClass?.containingFile
                        ?: annotatedDeclaration.containingFile!!

                processContext.codeGenerator.createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceFile),
                    packageName = packageName,
                    fileName = "CopyMapping__${annotatedDeclaration.underPackageName}",
                ) {
                    mappings.forEach { mapping ->
                        with(processContext.options, processContext.logger) {
                            it.appendCopyFunction(
                                source = mapping.sourceClass,
                                target = mapping.targetClass,
                                omitPackages = listOf("kotlin", packageName.asString()),
                                generateSourceAnnotation =
                                    GenerateSourceAnnotation.CopyMapping(annotation = mapping.rawAnnotation),
                                annotated = annotatedDeclaration,
                            )

                            if (mapping.canReverse) {
                                // The reverse function shares the same annotation; `reversed` swaps
                                // each (source -> target) property mapping for this direction.
                                it.appendCopyFunction(
                                    source = mapping.targetClass,
                                    target = mapping.sourceClass,
                                    omitPackages = listOf("kotlin", packageName.asString()),
                                    generateSourceAnnotation =
                                        GenerateSourceAnnotation.CopyMapping(
                                            annotation = mapping.rawAnnotation,
                                            reversed = true,
                                        ),
                                    annotated = annotatedDeclaration,
                                )
                            }
                        }
                    }
                }
            }
    }

    return invalidCopyMappingTargets
}

// ---------------------------------------------------------------------------
// Diagnostic helpers
// ---------------------------------------------------------------------------

private fun KSPLogger.reportCopyMappingMissingSource(annotatedDeclaration: KSClassDeclaration) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "source parameter is required in @${CopyMapping::class.simpleName}",
            solution = "Specify source class in @${CopyMapping::class.simpleName}",
        ),
        annotatedDeclaration,
    )
}

private fun KSPLogger.reportCopyMappingMissingTarget(annotatedDeclaration: KSClassDeclaration) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "target parameter is required in @${CopyMapping::class.simpleName}",
            solution = "Specify target class in @${CopyMapping::class.simpleName}",
        ),
        annotatedDeclaration,
    )
}
