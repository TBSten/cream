package me.tbsten.cream.ksp.testing.generator.util

import io.kotest.property.Arb
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorValue

/**
 * Transforms each representative value's [label][GeneratorValue.label] with [transform], leaving the
 * values and the [Arb] untouched (the [Arb] carries no labels). The dual of [map], which transforms
 * values and keeps labels.
 */
internal fun <T> Generator<T>.mapLabel(transform: (String?) -> String?): Generator<T> {
    val source = this

    return object : Generator<T> {
        override fun representativeValues(): Sequence<GeneratorValue<T>> = source.representativeValues().map { GeneratorValue(label = transform(it.label), value = it.value) }

        override fun arb(): Arb<T> = source.arb()
    }
}
