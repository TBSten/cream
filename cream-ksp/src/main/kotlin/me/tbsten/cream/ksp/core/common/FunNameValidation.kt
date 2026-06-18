package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.util.lines

/**
 * Build the [InvalidCreamUsageException] reported when a plain-literal [funNameTemplate] would
 * produce more than one identically named function. Single source of truth for the message.
 */
private fun invalidFunNameException(
    funNameTemplate: String,
    annotationSimpleName: String,
    declarationFullName: String,
): InvalidCreamUsageException =
    InvalidCreamUsageException(
        message =
            lines(
                "@$annotationSimpleName on $declarationFullName sets a fixed funName \"$funNameTemplate\",",
                "but it generates more than one function (multiple targets or sources, a sealed",
                "target, or a reversible mapping). Those functions would all share that name and collide.",
            ),
        solution =
            lines(
                "Include a naming token so each generated function gets a distinct name, e.g.",
                "  funName = \"to\" + CopyTargetSimpleName",
                "or split the declaration into separate annotations.",
            ),
    )

/** Outcome of [validateFunName]. */
internal enum class FunNameValidity {
    Valid,
    Invalid,
}

internal val FunNameValidity.isValid: Boolean get() = this == FunNameValidity.Valid

/**
 * Run [block] (typically a non-local `return` that skips the offending unit) when the funName is
 * [FunNameValidity.Invalid]. A no-op when it is [FunNameValidity.Valid].
 */
internal inline fun FunNameValidity.onInvalid(block: () -> Unit) {
    if (this == FunNameValidity.Invalid) block()
}

/**
 * Validate this [GenerateSourceAnnotation]'s funName template. When the annotation generates more
 * than one function ([generatesMultipleFunctions]) a plain-literal template (no naming token) makes
 * all of them share one name and collide; a template containing any token is fine because each
 * generated function then derives a distinct name from its own target. cream performs no other
 * funName validation (an otherwise illegal Kotlin name simply fails to compile at the use site).
 *
 * The funName template and annotation name come from this [GenerateSourceAnnotation]; only the
 * [generatesMultipleFunctions] condition and the diagnostic anchor ([declarationFullName] for the
 * message text, [ksNode] for the position) are passed in.
 *
 * @return [FunNameValidity.Valid] when the funName is acceptable; [FunNameValidity.Invalid] when it
 * was rejected and a clean positioned `COMPILATION_ERROR` has already been reported (the caller must
 * skip the offending unit, e.g. via `onInvalid { return@forEach }`, so no partial file is emitted).
 */
context(logger: KSPLogger)
internal fun GenerateSourceAnnotation.validateFunName(
    generatesMultipleFunctions: Boolean,
    declarationFullName: String,
    ksNode: KSNode?,
): FunNameValidity {
    if (!generatesMultipleFunctions) return FunNameValidity.Valid
    if (containsAnyCopyFunNameToken(funNameTemplate)) return FunNameValidity.Valid
    logger.error(
        invalidFunNameException(funNameTemplate, annotationSimpleName, declarationFullName).message.orEmpty(),
        ksNode,
    )
    return FunNameValidity.Invalid
}
