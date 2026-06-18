package me.tbsten.cream.ksp.feature.combineMapping

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.CombineMapping
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.combineFun.appendCombineToFunction
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.asClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.copyVisibilityArgument
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.extractKDoc
import me.tbsten.cream.ksp.core.common.extractPropertyMappings
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.funNameTemplate
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
 * @property propertyMappings List of property name mappings (source property name -> target property name)
 * @property kdocDescription User-provided KDoc description (from `kdoc.description`)
 * @property kdocExamples User-provided KDoc examples (from `kdoc.examples`)
 */
private data class CombineMappingInfo(
    val sourceClasses: List<KSClassDeclaration>,
    val targetClass: KSClassDeclaration,
    val propertyMappings: List<Pair<String, String>>,
    val kdocDescription: String,
    val kdocExamples: List<String>,
    val visibility: CopyVisibility,
    val funNameTemplate: String,
    /** Typed proxy for [GenerateSourceAnnotation.CombineMapping.annotation]. */
    val typedAnnotation: me.tbsten.cream.CombineMapping,
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
    typedAnnotation: me.tbsten.cream.CombineMapping,
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

    val propertyMaps = annotation.extractPropertyMappings()

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

    val (kdocDescription, kdocExamples) = annotation.extractKDoc()

    val visibility = annotation.copyVisibilityArgument()

    return CombineMappingInfo(
        sourceClasses = sourceClasses,
        targetClass = targetClass,
        propertyMappings = propertyMaps,
        kdocDescription = kdocDescription,
        kdocExamples = kdocExamples,
        visibility = visibility,
        funNameTemplate = annotation.funNameTemplate(),
        typedAnnotation = typedAnnotation,
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
        val typedAnnotations = annotatedDeclaration.getAnnotationsByType(CombineMapping::class).toList()
        val parsedMappings =
            rawAnnotations
                .zip(typedAnnotations)
                .map { (rawAnnotation, typedAnnotation) ->
                    parseCombineMapping(rawAnnotation, typedAnnotation, annotatedDeclaration, processContext.logger)
                }.toList()
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
                                    GenerateSourceAnnotation.CombineMapping(
                                        annotation = mapping.typedAnnotation,
                                        propertyMappings = mapping.propertyMappings,
                                    ),
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
