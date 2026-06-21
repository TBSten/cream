package me.tbsten.cream.ksp.testing.generator.util

import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorBuilder
import me.tbsten.cream.ksp.testing.generator.generator

/**
 * Replaces this generator's deterministic [representativeValues][Generator.representativeValues] with
 * a hand-picked set registered via [builder] (the same `case` / `"label" case value` DSL as
 * [generator]), while keeping the original [arb][Generator.arb] untouched.
 *
 * Use it when the derived representative set is too large or noisy for snapshot / example tests but
 * the full space is still wanted for property tests: e.g. curate a few readable [Generator]s on top
 * of a `combine(...)` whose cartesian product would otherwise yield far too many representatives.
 */
internal fun <T> Generator<T>.withRepresentativeValues(builder: GeneratorBuilder<T>.() -> Unit): Generator<T> {
    val base = this
    return generator<T> {
        builder()
        base.arb()
    }
}
