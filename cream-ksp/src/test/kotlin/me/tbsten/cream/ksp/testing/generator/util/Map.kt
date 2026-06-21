package me.tbsten.cream.ksp.testing.generator.util

import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorValue

/** Maps both the representative values and the [Arb] of this generator, preserving labels. */
internal fun <T, R> Generator<T>.map(transform: (T) -> R): Generator<R> {
    val source = this

    return object : Generator<R> {
        override fun representativeValues(): Sequence<GeneratorValue<R>> =
            sequence {
                source.representativeValues().forEach {
                    val mapped = transform(it.value)
                    val label = it.label
                    yield(GeneratorValue(label, mapped))
                }
            }

        override fun arb(): Arb<R> = source.arb().map(transform)
    }
}
