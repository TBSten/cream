package me.tbsten.cream.ksp.process

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.CombineMapping
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultCopyFunctionName
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
import me.tbsten.cream.ksp.util.funNameTemplate
import me.tbsten.cream.ksp.util.isSealed
import me.tbsten.cream.ksp.util.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.util.resolveToClassDeclaration
import me.tbsten.cream.ksp.util.underPackageName

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
                logger.reportCreamError(
                    InvalidCreamUsageException(
                        message = "sources parameter is required in @${CombineMapping::class.simpleName}",
                        solution = "Specify at least 2 source classes in @${CombineMapping::class.simpleName}",
                    ),
                    annotatedDeclaration,
                )
                return null
            }

    val targetType =
        annotation.arguments
            .firstOrNull { it.name?.asString() == "target" }
            ?.value as? KSType
            ?: run {
                logger.reportCreamError(
                    InvalidCreamUsageException(
                        message = "target parameter is required in @${CombineMapping::class.simpleName}",
                        solution = "Specify target class in @${CombineMapping::class.simpleName}",
                    ),
                    annotatedDeclaration,
                )
                return null
            }

    val propertyMaps = annotation.extractPropertyMappings()

    val sourceClasses =
        sourcesTypes.mapNotNull { sourceType ->
            (sourceType as? KSType)?.declaration?.resolveToClassDeclaration()
        }

    if (sourceClasses.size < 2) {
        logger.reportCreamError(
            InvalidCreamUsageException(
                message = "@${CombineMapping::class.simpleName} requires at least 2 source classes, but got ${sourceClasses.size}.",
                solution = "Specify at least 2 source classes in @${CombineMapping::class.simpleName}.sources",
            ),
            annotatedDeclaration,
        )
        return null
    }

    sourceClasses.forEach { sourceClass ->
        if (sourceClass.classKind != ClassKind.CLASS &&
            sourceClass.classKind != ClassKind.ANNOTATION_CLASS
        ) {
            logger.reportCreamError(
                InvalidCreamUsageException(
                    message = "${sourceClass.fullName} (Specified in @${CombineMapping::class.simpleName}.sources) must be a class.",
                    solution = "Specify a class in @${CombineMapping::class.simpleName}.sources",
                ),
                annotatedDeclaration,
            )
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
    )
}

internal fun CreamSymbolProcessor.processCombineMapping(resolver: Resolver): List<KSAnnotated> {
    val (combineMappingTargets, invalidCombineMappingTargets) =
        resolver
            .getSymbolsWithAnnotation(
                annotationName = CombineMapping::class.fullName,
            ).partition { it.validate() }

    combineMappingTargets.forEach { target ->
        val annotatedDeclaration =
            (target as? KSClassDeclaration)
                ?: run {
                    logger.reportCreamError(
                        InvalidCreamUsageException(
                            message = "@${CombineMapping::class.simpleName} must be applied to a class.",
                            solution = "Please apply @${CombineMapping::class.simpleName} to a `class` or `object`",
                        ),
                        target,
                    )
                    return@forEach
                }

        // Extract all CombineMapping annotations from the target. A malformed annotation reports a
        // clean diagnostic and yields null; skip the whole declaration so no partial file is emitted.
        val parsedMappings =
            target
                .annotationsOf(CombineMapping::class)
                .map { annotation -> parseCombineMapping(annotation, annotatedDeclaration, logger) }
                .toList()
        if (parsedMappings.any { it == null }) return@forEach
        val combineMappings = parsedMappings.filterNotNull()

        // Group by first source class package to create one file per source package
        combineMappings
            .groupBy { it.sourceClasses.first().packageName }
            .forEach { (packageName, mappings) ->
                // Use the first source class's file for dependencies
                val sourceFile =
                    mappings
                        .first()
                        .sourceClasses
                        .first()
                        .containingFile
                        ?: annotatedDeclaration.containingFile!!

                codeGenerator.createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceFile),
                    packageName = packageName,
                    fileName = "CombineMapping__${annotatedDeclaration.underPackageName}",
                ) {
                    mappings.forEach { mapping ->
                        val primarySource = mapping.sourceClasses.first()
                        val otherSources = mapping.sourceClasses.drop(1)

                        it.appendCombineToFunction(
                            primarySource = primarySource,
                            otherSources = otherSources,
                            target = mapping.targetClass,
                            options = options,
                            omitPackages = listOf("kotlin", packageName.asString()),
                            generateSourceAnnotation =
                                GenerateSourceAnnotation.CombineMapping(
                                    annotationTarget = annotatedDeclaration,
                                    propertyMappings = mapping.propertyMappings,
                                    kdocDescription = mapping.kdocDescription,
                                    kdocExamples = mapping.kdocExamples,
                                ),
                            visibility = mapping.visibility,
                            funNameTemplate = mapping.funNameTemplate,
                            logger = logger,
                        )
                    }
                }
            }
    }

    return invalidCombineMappingTargets
}
