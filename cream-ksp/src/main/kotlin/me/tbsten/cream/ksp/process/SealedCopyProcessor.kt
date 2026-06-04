package me.tbsten.cream.ksp.process

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.NonCopyableStrategy
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.CreamSymbolProcessor
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.transform.appendSealedCopyFunction
import me.tbsten.cream.ksp.transform.resolveSealedCopyFunName
import me.tbsten.cream.ksp.util.annotationsOf
import me.tbsten.cream.ksp.util.copyVisibilityArgument
import me.tbsten.cream.ksp.util.createNewKotlinFile
import me.tbsten.cream.ksp.util.extractKDoc
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.funNameTemplate
import me.tbsten.cream.ksp.util.isSealed
import me.tbsten.cream.ksp.util.lines
import me.tbsten.cream.ksp.util.underPackageName

internal fun CreamSymbolProcessor.processSealedCopy(resolver: Resolver): List<KSAnnotated> {
    val (sealedCopyTargets, invalidSealedCopyTargets) =
        resolver
            .getSymbolsWithAnnotation(
                annotationName = SealedCopy::class.fullName,
            ).partition { it.validate() }

    sealedCopyTargets.forEach { annotated ->
        if (annotated !is KSClassDeclaration) {
            logger.reportCreamError(
                InvalidCreamUsageException(
                    message = "@${SealedCopy::class.simpleName} must be applied to a sealed class/interface.",
                    solution = "Please apply @${SealedCopy::class.simpleName} to a `sealed class` or `sealed interface`.",
                ),
                annotated,
            )
            return@forEach
        }
        if (!annotated.isSealed()) {
            // Avoid `fullName`, which throws UnknownCreamException when qualifiedName is null
            // (e.g. local/anonymous declarations) and would mask this InvalidCreamUsageException.
            val displayName = annotated.qualifiedName?.asString() ?: annotated.simpleName.asString()
            logger.reportCreamError(
                InvalidCreamUsageException(
                    message =
                        "@${SealedCopy::class.simpleName} must be applied to a sealed class/interface, " +
                            "but $displayName is not sealed.",
                    solution = "Make $displayName a `sealed class` or `sealed interface`.",
                ),
                annotated,
            )
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
                        options = options,
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
            val displayName = annotated.qualifiedName?.asString() ?: annotated.simpleName.asString()
            logger.reportCreamError(
                InvalidCreamUsageException(
                    message =
                        lines(
                            "@${SealedCopy::class.simpleName} on $displayName generates more than one function named \"$duplicateFunName\".",
                            "Stacked @${SealedCopy::class.simpleName} annotations are written to one file, so each must produce a distinct name.",
                        ),
                    solution =
                        lines(
                            "Give each @${SealedCopy::class.simpleName} a distinct funName, e.g. funName = \"copyOrNull\".",
                        ),
                ),
                annotated,
            )
            return@forEach
        }

        // Warn for @SealedCopy.Exclude on non-abstract properties (no-op: they are not in copy())
        annotated
            .getAllProperties()
            .filter { !it.isAbstract() && it.annotationsOf(SealedCopy.Exclude::class).any() }
            .forEach { prop ->
                logger.warn(
                    "@Exclude on '${prop.simpleName.asString()}' has no effect: not a matched property",
                    prop,
                )
            }

        codeGenerator
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
                        sealedAnnotation.arguments
                            .firstOrNull { it.name?.asString() == "nonCopyableStrategy" }
                            ?.value
                            ?.toNonCopyableStrategy()
                            ?: NonCopyableStrategy.ERROR

                    val (kdocDescription, kdocExamples) = sealedAnnotation.extractKDoc()

                    val visibility = sealedAnnotation.copyVisibilityArgument()

                    it.appendSealedCopyFunction(
                        sealedClass = annotated,
                        funName = funName,
                        nonCopyableStrategy = nonCopyableStrategy,
                        omitPackages =
                            listOf(
                                "kotlin",
                                annotated.packageName.asString(),
                            ),
                        generateSourceAnnotation =
                            GenerateSourceAnnotation.SealedCopy(
                                annotationTarget = annotated,
                                kdocDescription = kdocDescription,
                                kdocExamples = kdocExamples,
                            ),
                        visibility = visibility,
                        logger = logger,
                    )
                }
            }
    }

    return invalidSealedCopyTargets
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
