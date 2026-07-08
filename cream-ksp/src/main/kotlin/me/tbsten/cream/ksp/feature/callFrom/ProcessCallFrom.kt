package me.tbsten.cream.ksp.feature.callFrom

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import me.tbsten.cream.CallFrom
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.callFrom.appendCallFromFunction
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.omitPackagesFor
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.resolveClassListOrReport
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.safeCast
import me.tbsten.cream.ksp.util.with

private val annotationName = CallFrom::class.simpleName!!

/**
 * `@CallFrom` entry point: discover annotated functions, validate each
 * ([validateCallFromTargetFunction] & friends in `CallFromValidation.kt`), reject bridges that
 * would collide ([filterOutCollidingUnits]), then generate one file per function *name* — all
 * same-name overloads share a file, since their generated file names would collide.
 */
context(processContext: ProcessContext)
internal fun processCallFrom(): List<KSAnnotated> =
    with(processContext.logger, processContext.options) {
        // inDepth = true so local functions are also surfaced: the design requires a positioned
        // error on `@CallFrom` local functions, but the default (inDepth = false) never returns
        // local declarations, which would silently ignore the annotation instead.
        val (callFromFunctions, invalidCallFromFunctions) =
            processContext.resolver
                .getSymbolsWithAnnotation(
                    annotationName = CallFrom::class.fullName,
                    inDepth = true,
                ).partition { it.validate() }

        val units = callFromFunctions.mapNotNull { buildGenerationUnit(it) }
        val survivingUnits = processContext.logger.filterOutCollidingUnits(processContext.resolver, units)

        survivingUnits
            .groupBy {
                it.targetFunction.packageName
                    .asString() to it.fileName
            }.values
            .forEach { groupUnits ->
                val representative = groupUnits.first()
                val originatingFiles =
                    groupUnits
                        .mapNotNull { it.targetFunction.containingFile }
                        .distinct()
                processContext.codeGenerator
                    .createNewKotlinFile(
                        dependencies = Dependencies(aggregating = true, *originatingFiles.toTypedArray()),
                        packageName = representative.targetFunction.packageName,
                        fileName = representative.fileName,
                    ) { appendable ->
                        groupUnits.forEach { unit ->
                            unit.sourceClasses.forEach { sourceClass ->
                                appendable.appendCallFromFunction(
                                    source = sourceClass,
                                    targetFunction = unit.targetFunction,
                                    omitPackages = omitPackagesFor(unit.targetFunction.packageName),
                                    visibilityModifier = unit.visibilityModifier,
                                    generateSourceAnnotation = unit.generateSourceAnnotation,
                                )
                            }
                        }
                    }
            }

        return invalidCallFromFunctions
    }

/**
 * Validate one annotated symbol and assemble its [CallFromGenerationUnit], or return `null`
 * after reporting a positioned error. Nothing is generated for a `null`, so no partial file can
 * be emitted for an invalid function.
 */
context(processContext: ProcessContext)
private fun buildGenerationUnit(annotated: KSAnnotated): CallFromGenerationUnit? =
    with(processContext.logger, processContext.options) {
        buildGenerationUnitImpl(annotated)
    }

context(logger: KSPLogger, options: CreamOptions)
private fun buildGenerationUnitImpl(annotated: KSAnnotated): CallFromGenerationUnit? {
    val targetFunction =
        annotated.safeCast<KSFunctionDeclaration>() ?: run {
            logger.reportCreamError(
                InvalidCreamUsageException(
                    message = "@$annotationName must be applied to a function.",
                    solution = "Please apply @$annotationName to a `fun` declaration.",
                ),
                annotated,
            )
            return null
        }

    if (!logger.validateCallFromTargetFunction(targetFunction)) return null

    // Read directly from KSAnnotation rather than via getAnnotationsByType: the typed
    // proxy throws NoSuchElementException on AA-backed KSP2 when accessing a field left
    // at its default. The raw arguments list correctly omits absent values.
    val callFromAnnotations = annotated.annotationsOf(CallFrom::class)

    // CallFrom.sources: List<KClass<*>>. If any source cannot be resolved to a class, an
    // error has been reported and we skip this function so no partial file is emitted.
    val sourceClasses =
        callFromAnnotations.resolveClassListOrReport("sources", annotationName, targetFunction) ?: return null

    if (!logger.validateSourcesNotEmpty(targetFunction, sourceClasses)) return null
    if (!logger.validateNoDuplicatedSources(targetFunction, sourceClasses)) return null
    if (!logger.validateNoSourceParamNameCollision(targetFunction, sourceClasses)) return null
    if (!logger.validateNoFatallyDeprecatedReferences(targetFunction, sourceClasses)) return null

    val generateSourceAnnotation =
        GenerateSourceAnnotation.CallFrom(
            annotation = callFromAnnotations.firstOrNull() ?: return null,
        )

    val visibilityModifier =
        logger.resolveCallFromBridgeVisibility(
            targetFunction = targetFunction,
            sourceClasses = sourceClasses,
            annotationVisibility = generateSourceAnnotation.visibility,
            options = options,
        ) ?: return null

    return CallFromGenerationUnit(
        targetFunction = targetFunction,
        sourceClasses = sourceClasses,
        generateSourceAnnotation = generateSourceAnnotation,
        visibilityModifier = visibilityModifier,
    )
}
