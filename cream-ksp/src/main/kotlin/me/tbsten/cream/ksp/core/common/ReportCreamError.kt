package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSNode
import me.tbsten.cream.ksp.CreamException
import me.tbsten.cream.ksp.InvalidCreamUsageException

/**
 * Report a user-misuse [CreamException] (invalid annotation usage / invalid option) as a clean,
 * positioned `COMPILATION_ERROR` via [KSPLogger.error], rather than letting it propagate as a raw
 * `throw` (which KSP reports as an `INTERNAL_ERROR` — a "processor crashed" stack trace plus, in
 * the worst case, a half-written generated file).
 *
 * [ksNode] anchors the diagnostic to the offending declaration / annotation so the file and line
 * are shown and IDE navigation works. Pass `null` only for errors that have no source location
 * (e.g. an invalid KSP option, which comes from the build script, not the user's source).
 *
 * Mirrors the `reportRejection` helper used by the target-kind dispatcher
 * ([me.tbsten.cream.ksp.core.copyFun.appendCopyFunction]); both funnel cream's user-facing errors
 * through `logger.error` so every misuse surfaces the same way. After calling this, the caller
 * MUST stop processing the offending unit (e.g. `return@forEach`) so no partial file is emitted.
 */
internal fun KSPLogger.reportCreamError(
    exception: CreamException,
    ksNode: KSNode?,
) {
    // CreamException always builds a non-null message; orEmpty() keeps the call non-null without
    // an unsafe assertion.
    error(exception.message.orEmpty(), ksNode)
}

/**
 * Casts this [KSAnnotated] to [KSDeclaration], reporting a clean error and returning `null` when
 * the cast fails. Callers must stop processing the unit on `null` (e.g. `?: return@forEach`).
 *
 * Use when an annotation requires a declaration target (class / interface / typealias) and may be
 * incorrectly applied to a non-declaration (e.g. a function parameter).
 */
context(logger: KSPLogger)
internal fun KSAnnotated.asDeclarationOrReport(annotationSimpleName: String): KSDeclaration? =
    this as? KSDeclaration
        ?: run {
            logger.reportCreamError(
                InvalidCreamUsageException(
                    message = "@$annotationSimpleName must be applied to a class, interface, or typealias.",
                    solution = "Please apply @$annotationSimpleName to `class`, `interface`, or `typealias`",
                ),
                this,
            )
            null
        }

/**
 * Casts this [KSAnnotated] to [KSClassDeclaration], reporting a clean error and returning `null` when
 * the cast fails. Callers must stop processing the unit on `null` (e.g. `?: return@forEach`).
 *
 * Use for annotations that require a `class` / `object` target (e.g. `@CopyMapping` /
 * `@CombineMapping`), which may be incorrectly applied to a non-class declaration.
 */
context(logger: KSPLogger)
internal fun KSAnnotated.asClassDeclarationOrReport(annotationSimpleName: String): KSClassDeclaration? =
    this as? KSClassDeclaration
        ?: run {
            logger.reportCreamError(
                InvalidCreamUsageException(
                    message = "@$annotationSimpleName must be applied to a class.",
                    solution = "Please apply @$annotationSimpleName to a `class` or `object`",
                ),
                this,
            )
            null
        }
