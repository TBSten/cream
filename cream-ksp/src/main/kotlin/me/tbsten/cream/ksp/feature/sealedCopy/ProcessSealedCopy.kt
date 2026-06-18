package me.tbsten.cream.ksp.feature.sealedCopy

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.NonCopyableStrategy
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.ProcessContext
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.createNewKotlinFile
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.funNameTemplate
import me.tbsten.cream.ksp.core.common.reportCreamError
import me.tbsten.cream.ksp.core.common.resolveSealedCopyFunName
import me.tbsten.cream.ksp.core.common.underPackageName
import me.tbsten.cream.ksp.core.sealedCopy.appendSealedCopyFunction
import me.tbsten.cream.ksp.util.ksp.getArgument
import me.tbsten.cream.ksp.util.ksp.isSealed
import me.tbsten.cream.ksp.util.lines
import me.tbsten.cream.ksp.util.with

private val annotationName = SealedCopy::class.simpleName!!

context(processContext: ProcessContext)
internal fun processSealedCopy(): List<KSAnnotated> =
    with(processContext.logger, processContext.options) {
        val (sealedCopyTargets, invalidSealedCopyTargets) =
            processContext.resolver
                .getSymbolsWithAnnotation(
                    annotationName = SealedCopy::class.fullName,
                ).partition { it.validate() }

        sealedCopyTargets.forEach { annotated ->
            if (annotated !is KSClassDeclaration) {
                processContext.logger.reportSealedCopyNotADeclaration(annotated)
                return@forEach
            }
            if (!annotated.isSealed()) {
                processContext.logger.reportSealedCopyNotSealed(annotated)
                return@forEach
            }

            // Read directly from KSAnnotation rather than via getAnnotationsByType:
            // the typed proxy throws NoSuchElementException on AA-backed KSP2 when
            // accessing a field that wasn't given explicitly (the bare `@SealedCopy`
            // case). The raw arguments list correctly omits absent values, letting us
            // fall back to the documented defaults.
            val sealedAnnotations =
                annotated.annotations
                    .filter {
                        it.annotationType
                            .resolve()
                            .declaration.fullName == SealedCopy::class.qualifiedName
                    }.toList()
            if (sealedAnnotations.isEmpty()) {
                // Defensive: getSymbolsWithAnnotation matched but no SealedCopy
                // annotation could be resolved (e.g. broken classpath entry).
                return@forEach
            }

            // Resolve and validate every stacked @SealedCopy funName up front, before opening the
            // output file. @SealedCopy is @Repeatable and all variants are written to one file, so two
            // annotations that resolve to the same name would emit conflicting overloads — reject that
            // with a clear cream error instead of letting it fail at the user's compiler.
            val annotationsWithFunName =
                sealedAnnotations.map { sealedAnnotation ->
                    sealedAnnotation to
                        resolveSealedCopyFunName(
                            funNameTemplate = sealedAnnotation.funNameTemplate(),
                            sealedClass = annotated,
                            options = processContext.options,
                        )
                }
            val duplicateFunName =
                annotationsWithFunName
                    .groupingBy { (_, funName) -> funName }
                    .eachCount()
                    .entries
                    .firstOrNull { it.value > 1 }
                    ?.key
            if (duplicateFunName != null) {
                processContext.logger.reportSealedCopyDuplicateFunName(annotated, duplicateFunName)
                return@forEach
            }

            // Warn for @SealedCopy.Exclude on non-abstract properties (no-op: they are not in copy())
            annotated
                .getAllProperties()
                .filter { !it.isAbstract() && it.annotationsOf(SealedCopy.Exclude::class).any() }
                .forEach { prop ->
                    processContext.logger.warn(
                        "@Exclude on '${prop.simpleName.asString()}' has no effect: not a matched property",
                        prop,
                    )
                }

            processContext.codeGenerator
                .createNewKotlinFile(
                    dependencies =
                        Dependencies(
                            aggregating = true,
                            annotated.containingFile!!,
                        ),
                    packageName = annotated.packageName,
                    fileName = "SealedCopy__${annotated.underPackageName}",
                ) {
                    annotationsWithFunName.forEach { (sealedAnnotation, funName) ->
                        val nonCopyableStrategy =
                            sealedAnnotation
                                .getArgument<Any>("nonCopyableStrategy")
                                ?.toNonCopyableStrategy()
                                ?: NonCopyableStrategy.ERROR

                        // Pass the raw per-occurrence annotation: @SealedCopy is @Repeatable and each
                        // occurrence is its own variant, so GSA must read kdoc/visibility/funName from
                        // *this* occurrence rather than a `getAnnotationsByType().first()` proxy.
                        it.appendSealedCopyFunction(
                            sealedClass = annotated,
                            funName = funName,
                            nonCopyableStrategy = nonCopyableStrategy,
                            omitPackages =
                                listOf(
                                    "kotlin",
                                    annotated.packageName.asString(),
                                ),
                            generateSourceAnnotation = GenerateSourceAnnotation.SealedCopy(annotation = sealedAnnotation),
                        )
                    }
                }
        }

        return invalidSealedCopyTargets
    }

// ---------------------------------------------------------------------------
// Diagnostic helpers — each encapsulates one user-misuse error so call sites
// stay concise and the message text lives in exactly one place.
// ---------------------------------------------------------------------------

private fun KSPLogger.reportSealedCopyNotADeclaration(annotated: KSAnnotated) {
    reportCreamError(
        InvalidCreamUsageException(
            message = "@$annotationName must be applied to a sealed class/interface.",
            solution = "Please apply @$annotationName to a `sealed class` or `sealed interface`.",
        ),
        annotated,
    )
}

private fun KSPLogger.reportSealedCopyNotSealed(annotated: KSClassDeclaration) {
    // Avoid `fullName`, which throws UnknownCreamException when qualifiedName is null
    // (e.g. local/anonymous declarations) and would mask this InvalidCreamUsageException.
    val displayName = annotated.qualifiedName?.asString() ?: annotated.simpleName.asString()
    reportCreamError(
        InvalidCreamUsageException(
            message =
                "@$annotationName must be applied to a sealed class/interface, " +
                    "but $displayName is not sealed.",
            solution = "Make $displayName a `sealed class` or `sealed interface`.",
        ),
        annotated,
    )
}

private fun KSPLogger.reportSealedCopyDuplicateFunName(
    annotated: KSClassDeclaration,
    duplicateFunName: String,
) {
    val displayName = annotated.qualifiedName?.asString() ?: annotated.simpleName.asString()
    reportCreamError(
        InvalidCreamUsageException(
            message =
                lines(
                    "@$annotationName on $displayName generates more than one function named \"$duplicateFunName\".",
                    "Stacked @$annotationName annotations are written to one file, so each must produce a distinct name.",
                ),
            solution =
                lines(
                    "Give each @$annotationName a distinct funName, e.g. funName = \"copyOrNull\".",
                ),
        ),
        annotated,
    )
}

/**
 * Read an annotation argument's value (as returned by KSP — typically a `KSType` or
 * `KSClassDeclaration` for enum entries, but [Enum] / [String] are also accepted as a
 * defensive fallback) and resolve it to a [NonCopyableStrategy] entry. Returns `null`
 * when the value's simple name does not match any entry.
 */
private fun Any.toNonCopyableStrategy(): NonCopyableStrategy? {
    val entryName =
        when (this) {
            is KSClassDeclaration -> simpleName.asString()
            is KSType -> declaration.simpleName.asString()
            is Enum<*> -> name
            is String -> this
            else -> return null
        }
    return runCatching { NonCopyableStrategy.valueOf(entryName) }.getOrNull()
}
