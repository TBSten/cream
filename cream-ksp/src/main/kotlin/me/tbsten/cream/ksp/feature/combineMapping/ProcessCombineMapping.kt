package me.tbsten.cream.ksp.feature.combineMapping

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.CombineMapping
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.combineFun.appendCombineToFunction
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.asClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.omitPackagesFor
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.resolveToClassDeclaration
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.util.ksp.getArgument
import me.tbsten.cream.ksp.util.with

private val annotationName = CombineMapping::class.simpleName!!

/**
 * Data class to hold CombineMapping annotation information
 *
 * @property sourceClasses The source classes to combine from (at least 2 sources)
 * @property targetClass The target class for the mapping
 * @property rawAnnotation Raw annotation this mapping was parsed from; GSA derives KDoc / funName /
 *   property mappings from it.
 */
private data class CombineMappingInfo(
    val sourceClasses: List<KSClassDeclaration>,
    val targetClass: KSClassDeclaration,
    val rawAnnotation: KSAnnotation,
)

/**
 * Parse one `@CombineMapping` annotation into a [CombineMappingInfo]. On any user-misuse error
 * (missing `sources` / `target`, fewer than 2 sources, a non-class source, or a `target` that
 * cannot be resolved to a class), reports a clean positioned `COMPILATION_ERROR` via [logger]
 * (anchored at [annotatedDeclaration]) and returns `null` so the caller can skip the whole
 * declaration without crashing KSP.
 */
private fun parseCombineMapping(
    annotation: KSAnnotation,
    annotatedDeclaration: KSClassDeclaration,
    logger: KSPLogger,
): CombineMappingInfo? {
    val sourcesTypes =
        annotation.getArgument(CombineMapping::sources)
            ?: run {
                logger.reportCombineMappingMissingSources(annotatedDeclaration)
                return null
            }

    val targetType =
        annotation.getArgument(CombineMapping::target)
            ?: run {
                logger.reportCombineMappingMissingTarget(annotatedDeclaration)
                return null
            }

    val sourceClasses =
        sourcesTypes.mapNotNull { sourceType ->
            (sourceType as? KSType)?.declaration?.resolveToClassDeclaration()
        }

    if (sourceClasses.size < 2) {
        logger.reportCombineMappingInsufficientSources(annotatedDeclaration, sourceClasses.size)
        return null
    }

    sourceClasses.forEach { sourceClass ->
        if (sourceClass.classKind != ClassKind.CLASS &&
            sourceClass.classKind != ClassKind.ANNOTATION_CLASS
        ) {
            logger.reportCombineMappingInvalidSourceKind(annotatedDeclaration, sourceClass)
            return null
        }
    }

    val targetClass =
        targetType.declaration.resolveClassDeclarationOrReport(
            annotationName = annotationName,
            logger = logger,
            context = "Specified in @$annotationName.target",
            ksNode = annotatedDeclaration,
        ) ?: return null

    return CombineMappingInfo(
        sourceClasses = sourceClasses,
        targetClass = targetClass,
        rawAnnotation = annotation,
    )
}

context(processContext: ProcessContext)
internal fun processCombineMapping(): List<KSAnnotated> =
    with(processContext.logger, processContext.options) {
        val (combineMappingTargets, invalidCombineMappingTargets) =
            processContext.resolver
                .getSymbolsWithAnnotation(
                    annotationName = CombineMapping::class.fullName,
                ).partition { it.validate() }

        combineMappingTargets.forEach { target ->
            val annotatedDeclaration =
                target.asClassDeclarationOrReport(annotationName) ?: return@forEach

            // Extract all CombineMapping annotations from the target. A malformed annotation reports a
            // clean diagnostic and yields null; skip the whole declaration so no partial file is emitted.
            val rawAnnotations = target.annotationsOf(CombineMapping::class).toList()
            val parsedMappings =
                rawAnnotations.map { rawAnnotation ->
                    parseCombineMapping(rawAnnotation, annotatedDeclaration, processContext.logger)
                }
            if (parsedMappings.any { it == null }) return@forEach
            val combineMappings = parsedMappings.filterNotNull()

            val mappingPackage = annotatedDeclaration.packageName
            val omitPackages = omitPackagesFor(mappingPackage)

            // Depend on every referenced source/target file plus the holder so incremental
            // processing re-runs whenever any of them changes.
            val dependencyFiles =
                (
                    combineMappings.flatMap { mapping ->
                        mapping.sourceClasses.mapNotNull { it.containingFile } +
                            listOfNotNull(mapping.targetClass.containingFile)
                    } + listOfNotNull(annotatedDeclaration.containingFile)
                ).distinct()

            processContext.codeGenerator.createNewKotlinFile(
                dependencies = Dependencies(aggregating = true, *dependencyFiles.toTypedArray()),
                packageName = mappingPackage,
                fileName = "CombineMapping__${annotatedDeclaration.underPackageName}",
            ) {
                combineMappings.forEach { mapping ->
                    val primarySource = mapping.sourceClasses.firstOrNull() ?: return@forEach
                    val otherSources = mapping.sourceClasses.drop(1)

                    it.appendCombineToFunction(
                        primarySource = primarySource,
                        otherSources = otherSources,
                        target = mapping.targetClass,
                        omitPackages = omitPackages,
                        generateSourceAnnotation =
                            GenerateSourceAnnotation.CombineMapping(annotation = mapping.rawAnnotation),
                    )
                }
            }
        }

        return invalidCombineMappingTargets
    }

// ---------------------------------------------------------------------------
// Diagnostic helpers
// ---------------------------------------------------------------------------

private fun KSPLogger.reportCombineMappingMissingSources(annotatedDeclaration: KSClassDeclaration) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "sources parameter is required in @$annotationName",
            solution = "Specify at least 2 source classes in @$annotationName",
        ),
        annotatedDeclaration,
    )
}

private fun KSPLogger.reportCombineMappingMissingTarget(annotatedDeclaration: KSClassDeclaration) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "target parameter is required in @$annotationName",
            solution = "Specify target class in @$annotationName",
        ),
        annotatedDeclaration,
    )
}

private fun KSPLogger.reportCombineMappingInsufficientSources(
    annotatedDeclaration: KSClassDeclaration,
    actualCount: Int,
) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "@$annotationName requires at least 2 source classes, but got $actualCount.",
            solution = "Specify at least 2 source classes in @$annotationName.sources",
        ),
        annotatedDeclaration,
    )
}

private fun KSPLogger.reportCombineMappingInvalidSourceKind(
    annotatedDeclaration: KSClassDeclaration,
    sourceClass: KSClassDeclaration,
) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "${sourceClass.fullName} (Specified in @$annotationName.sources) must be a class.",
            solution = "Specify a class in @$annotationName.sources",
        ),
        annotatedDeclaration,
    )
}
