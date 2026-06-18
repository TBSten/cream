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
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.resolveToClassDeclaration
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.util.with

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
        annotation.arguments
            .firstOrNull { it.name?.asString() == "sources" }
            ?.value as? List<*>
            ?: run {
                logger.reportCombineMappingMissingSources(annotatedDeclaration)
                return null
            }

    val targetType =
        annotation.arguments
            .firstOrNull { it.name?.asString() == "target" }
            ?.value as? KSType
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
            annotationName = CombineMapping::class.simpleName!!,
            logger = logger,
            context = "Specified in @${CombineMapping::class.simpleName}.target",
            ksNode = annotatedDeclaration,
        ) ?: return null

    return CombineMappingInfo(
        sourceClasses = sourceClasses,
        targetClass = targetClass,
        rawAnnotation = annotation,
    )
}

context(processContext: ProcessContext)
internal fun processCombineMapping(): List<KSAnnotated> {
    val (combineMappingTargets, invalidCombineMappingTargets) =
        processContext.resolver
            .getSymbolsWithAnnotation(
                annotationName = CombineMapping::class.fullName,
            ).partition { it.validate() }

    combineMappingTargets.forEach { target ->
        val annotatedDeclaration =
            with(processContext.logger) { target.asClassDeclarationOrReport(CombineMapping::class.simpleName!!) } ?: return@forEach

        // Extract all CombineMapping annotations from the target. A malformed annotation reports a
        // clean diagnostic and yields null; skip the whole declaration so no partial file is emitted.
        val rawAnnotations = target.annotationsOf(CombineMapping::class).toList()
        val parsedMappings =
            rawAnnotations.map { rawAnnotation ->
                parseCombineMapping(rawAnnotation, annotatedDeclaration, processContext.logger)
            }
        if (parsedMappings.any { it == null }) return@forEach
        val combineMappings = parsedMappings.filterNotNull()

        // Group by first source class package to create one file per source package
        combineMappings
            .groupBy { it.sourceClasses.firstOrNull()?.packageName }
            .forEach { (packageName, mappings) ->
                if (packageName == null) return@forEach
                // Use the first source class's file for dependencies
                val sourceFile =
                    mappings
                        .firstOrNull()
                        ?.sourceClasses
                        ?.firstOrNull()
                        ?.containingFile
                        ?: annotatedDeclaration.containingFile!!

                processContext.codeGenerator.createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceFile),
                    packageName = packageName,
                    fileName = "CombineMapping__${annotatedDeclaration.underPackageName}",
                ) {
                    mappings.forEach { mapping ->
                        val primarySource = mapping.sourceClasses.firstOrNull() ?: return@forEach
                        val otherSources = mapping.sourceClasses.drop(1)

                        with(processContext.options, processContext.logger) {
                            it.appendCombineToFunction(
                                primarySource = primarySource,
                                otherSources = otherSources,
                                target = mapping.targetClass,
                                omitPackages = listOf("kotlin", packageName.asString()),
                                generateSourceAnnotation =
                                    GenerateSourceAnnotation.CombineMapping(annotation = mapping.rawAnnotation),
                                annotated = annotatedDeclaration,
                            )
                        }
                    }
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
            message = "sources parameter is required in @${CombineMapping::class.simpleName}",
            solution = "Specify at least 2 source classes in @${CombineMapping::class.simpleName}",
        ),
        annotatedDeclaration,
    )
}

private fun KSPLogger.reportCombineMappingMissingTarget(annotatedDeclaration: KSClassDeclaration) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "target parameter is required in @${CombineMapping::class.simpleName}",
            solution = "Specify target class in @${CombineMapping::class.simpleName}",
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
            message = "@${CombineMapping::class.simpleName} requires at least 2 source classes, but got $actualCount.",
            solution = "Specify at least 2 source classes in @${CombineMapping::class.simpleName}.sources",
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
            message = "${sourceClass.fullName} (Specified in @${CombineMapping::class.simpleName}.sources) must be a class.",
            solution = "Specify a class in @${CombineMapping::class.simpleName}.sources",
        ),
        annotatedDeclaration,
    )
}
