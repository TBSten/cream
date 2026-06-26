package me.tbsten.cream.ksp.testing.generator

import io.kotest.property.Arb

/**
 * A source of test inputs that serves two complementary roles:
 *
 * - [representativeValues] — a small, deterministic, hand-pickable set of values used by
 *   example / snapshot tests. Must stay stable across runs (no randomness, stable order).
 * - [arb] — a kotest [Arb] for property-based tests.
 *
 * The DSL entry point [generator] lives in `GeneratorBuilder.kt`; combinators ([map], [orNull],
 * [Generator.Companion.list], [toGenerator]) live in `GeneratorUtility.kt` and build their results
 * through the [generator] DSL.
 */
internal interface Generator<Value> {
    fun representativeValues(): Sequence<GeneratorValue<Value>>

    fun arb(): Arb<Value>

    companion object
}

/** A single representative value, optionally tagged with a human-readable [label]. */
internal data class GeneratorValue<Value>(
    val label: String? = null,
    val value: Value,
)
