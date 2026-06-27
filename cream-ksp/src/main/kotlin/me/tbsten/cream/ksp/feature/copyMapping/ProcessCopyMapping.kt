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
import me.tbsten.cream.ksp.core.common.isValid
import me.tbsten.cream.ksp.core.common.omitPackagesFor
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.core.common.validateFunName
import me.tbsten.cream.ksp.core.copyFun.appendCopyFunction
import me.tbsten.cream.ksp.util.ksp.getArgument
import me.tbsten.cream.ksp.util.ksp.isSealed
import me.tbsten.cream.ksp.util.with

private val annotationName = CopyMapping::class.simpleName!!

/**
 * Data class to hold CopyMapping annotation information
 *
 * @property sourceClass The source class for the mapping
 * @property targetClass The target class for the mapping
 * @property canReverse Whether bidirectional mapping is enabled
 * @property rawAnnotation Raw annotation this mapping was parsed from; GSA derives KDoc / funName /
 *   property mappings from it.
 */
private data class CopyMappingInfo(
    val sourceClass: KSClassDeclaration,
    val targetClass: KSClassDeclaration,
    val canReverse: Boolean,
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
        annotation.getArgument(CopyMapping::source)
            ?: run {
                logger.reportCopyMappingMissingSource(annotatedDeclaration)
                return null
            }

    val targetType =
        annotation.getArgument(CopyMapping::target)
            ?: run {
                logger.reportCopyMappingMissingTarget(annotatedDeclaration)
                return null
            }

    val canReverse =
        annotation.getArgument(CopyMapping::canReverse)
            ?: false

    val sourceClass =
        sourceType.declaration.resolveClassDeclarationOrReport(
            annotationName = annotationName,
            logger = logger,
            context = "Specified in @$annotationName.source",
            ksNode = annotatedDeclaration,
        ) ?: return null

    val targetClass =
        targetType.declaration.resolveClassDeclarationOrReport(
            annotationName = annotationName,
            logger = logger,
            context = "Specified in @$annotationName.target",
            ksNode = annotatedDeclaration,
        ) ?: return null

    return CopyMappingInfo(
        sourceClass = sourceClass,
        targetClass = targetClass,
        canReverse = canReverse,
        rawAnnotation = annotation,
    )
}

context(processContext: ProcessContext)
internal fun processCopyMapping(): List<KSAnnotated> =
    with(processContext.logger, processContext.options) {
        val (copyMappingTargets, invalidCopyMappingTargets) =
            processContext.resolver
                .getSymbolsWithAnnotation(
                    annotationName = CopyMapping::class.fullName,
                ).partition { it.validate() }

        copyMappingTargets.forEach { target ->
            val annotatedDeclaration =
                target.asClassDeclarationOrReport(annotationName) ?: return@forEach

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
            // produce more than one colliding function. Mirrors CopyTo/CopyFrom, which guard before
            // createNewKotlinFile so a rejected funName never leaves a partial generated file.
            val allFunNamesOk =
                copyMappings.all { mapping ->
                    GenerateSourceAnnotation
                        .CopyMapping(annotation = mapping.rawAnnotation)
                        .validateFunName(
                            generatesMultipleFunctions = mapping.canReverse || mapping.targetClass.isSealed(),
                            declarationFullName = annotatedDeclaration.fullName,
                            ksNode = annotatedDeclaration,
                        ).isValid
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
                            it.appendCopyFunction(
                                source = mapping.sourceClass,
                                target = mapping.targetClass,
                                omitPackages = omitPackagesFor(packageName),
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
                                    omitPackages = omitPackagesFor(packageName),
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

        return invalidCopyMappingTargets
    }

// ---------------------------------------------------------------------------
// Diagnostic helpers
// ---------------------------------------------------------------------------

private fun KSPLogger.reportCopyMappingMissingSource(annotatedDeclaration: KSClassDeclaration) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "source parameter is required in @$annotationName",
            solution = "Specify source class in @$annotationName",
        ),
        annotatedDeclaration,
    )
}

private fun KSPLogger.reportCopyMappingMissingTarget(annotatedDeclaration: KSClassDeclaration) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "target parameter is required in @$annotationName",
            solution = "Specify target class in @$annotationName",
        ),
        annotatedDeclaration,
    )
}
