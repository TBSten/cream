package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode
import me.tbsten.cream.ksp.CreamException

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
