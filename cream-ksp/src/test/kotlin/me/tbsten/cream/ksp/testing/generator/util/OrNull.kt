package me.tbsten.cream.ksp.testing.generator.util

import io.kotest.property.Arb
import io.kotest.property.arbitrary.orNull
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorValue

/**
 * Makes a generator nullable: prepends a `null` representative value and lets the [Arb] emit `null`
 * with [nullProbability].
 */
internal fun <Value> Generator<Value>.orNull(nullProbability: Double = 0.1): Generator<Value?> {
    val source = this

    return object : Generator<Value?> {
        override fun representativeValues(): Sequence<GeneratorValue<Value?>> =
            sequence {
                yield(GeneratorValue<Value?>(label = "null", value = null))
                source.representativeValues().forEach {
                    yield(GeneratorValue<Value?>(it.label, it.value))
                }
            }

        override fun arb(): Arb<Value?> = source.arb().orNull(nullProbability)
    }
}
