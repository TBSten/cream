package me.tbsten.cream.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import me.tbsten.cream.ksp.options.CreamOptions

/**
 * Per-round processing infrastructure shared by every feature entry point.
 *
 * Bundles the four cross-cutting dependencies that used to be threaded individually — [resolver]
 * was a `process()` parameter passed to each `processXxx`, [options] / [codeGenerator] / [logger]
 * were reached through the [CreamSymbolProcessor] receiver, and [logger] in particular travelled as
 * a nullable argument deep into the generators. Collecting them here lets a feature take a single
 * `context(ctx: ProcessContext)` instead of a receiver plus a [Resolver] parameter.
 *
 * [logger] is **non-null**: cream always has a logger from the KSP environment, so the generators no
 * longer carry a `KSPLogger?` fallback. The old `null` branch threw instead of reporting cleanly and
 * was unreachable (every caller threaded the environment logger), so dropping it leaves behaviour
 * unchanged — the snapshot suite stays byte-identical.
 *
 * This is leaf infrastructure: it must not depend on `feature` or `core`. The generation layer takes
 * the narrower capabilities it needs (e.g. `logger`) directly rather than the whole context.
 */
internal class ProcessContext(
    val resolver: Resolver,
    val options: CreamOptions,
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
)
