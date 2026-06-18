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
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.extractKDoc
import me.tbsten.cream.ksp.core.common.extractPropertyMappings
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.funNameTemplate
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.requireFunNameSupportsFanout
import me.tbsten.cream.ksp.core.common.resolveClassDeclarationOrReport
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.core.copyFun.appendCopyFunction
import me.tbsten.cream.ksp.util.ksp.isSealed

/**
 * Data class to hold CopyMapping annotation information
 *
 * @property sourceClass The source class for the mapping
 * @property targetClass The target class for the mapping
 * @property canReverse Whether bidirectional mapping is enabled
 * @property propertyMappings List of property name mappings (source property name -> target property name)
 * @property kdocDescription User-provided KDoc description (from `kdoc.description`)
 * @property kdocExamples User-provided KDoc examples (from `kdoc.examples`)
 */
private data class CopyMappingInfo(
    val sourceClass: KSClassDeclaration,
    val targetClass: KSClassDeclaration,
    val canReverse: Boolean,
    val propertyMappings: List<Pair<String, String>>,
    val kdocDescription: String,
    val kdocExamples: List<String>,
    val funNameTemplate: String,
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
                logger.reportCreamError(
                    InvalidCreamUsageException(
                        message = "source parameter is required in @${CopyMapping::class.simpleName}",
                        solution = "Specify source class in @${CopyMapping::class.simpleName}",
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
                        message = "target parameter is required in @${CopyMapping::class.simpleName}",
                        solution = "Specify target class in @${CopyMapping::class.simpleName}",
                    ),
                    annotatedDeclaration,
                )
                return null
            }

    val canReverse =
        annotation.arguments
            .firstOrNull { it.name?.asString() == "canReverse" }
            ?.value as? Boolean
            ?: false

    val propertyMaps = annotation.extractPropertyMappings()

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

    val (kdocDescription, kdocExamples) = annotation.extractKDoc()

    return CopyMappingInfo(
        sourceClass = sourceClass,
        targetClass = targetClass,
        canReverse = canReverse,
        propertyMappings = propertyMaps,
        kdocDescription = kdocDescription,
        kdocExamples = kdocExamples,
        funNameTemplate = annotation.funNameTemplate(),
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
            (target as? KSClassDeclaration)
                ?: run {
                    processContext.logger.reportCreamError(
                        InvalidCreamUsageException(
                            message = "@${CopyMapping::class.simpleName} must be applied to a class.",
                            solution = "Please apply @${CopyMapping::class.simpleName} to a `class` or `object`",
                        ),
                        target,
                    )
                    return@forEach
                }

        // Extract all CopyMapping annotations from the target. A malformed annotation reports a
        // clean diagnostic and yields null; skip the whole declaration so no partial file is emitted.
        val parsedMappings =
            target
                .annotationsOf(CopyMapping::class)
                .map { annotation -> parseCopyMapping(annotation, annotatedDeclaration, processContext.logger) }
                .toList()
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
                        with(processContext.options) {
                            with(processContext.logger) {
                                it.appendCopyFunction(
                                    source = mapping.sourceClass,
                                    target = mapping.targetClass,
                                    omitPackages = listOf("kotlin", packageName.asString()),
                                    generateSourceAnnotation =
                                        GenerateSourceAnnotation.CopyMapping(
                                            annotationTarget = annotatedDeclaration,
                                            propertyMappings = mapping.propertyMappings,
                                            kdocDescription = mapping.kdocDescription,
                                            kdocExamples = mapping.kdocExamples,
                                        ),
                                    notCopyToObject = false,
                                    funNameTemplate = mapping.funNameTemplate,
                                )

                                if (mapping.canReverse) {
                                    val reversePropertyMappings =
                                        mapping.propertyMappings.map { (source, target) ->
                                            target to source
                                        }
                                    it.appendCopyFunction(
                                        source = mapping.targetClass,
                                        target = mapping.sourceClass,
                                        omitPackages = listOf("kotlin", packageName.asString()),
                                        generateSourceAnnotation =
                                            GenerateSourceAnnotation.CopyMapping(
                                                annotationTarget = annotatedDeclaration,
                                                propertyMappings = reversePropertyMappings,
                                                kdocDescription = mapping.kdocDescription,
                                                kdocExamples = mapping.kdocExamples,
                                            ),
                                        notCopyToObject = false,
                                        funNameTemplate = mapping.funNameTemplate,
                                    )
                                }
                            }
                        }
                    }
                }
            }
    }

    return invalidCopyMappingTargets
}
