package me.tbsten.cream.ksp.testing.generator.util

import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorValue

/** A generator that always yields a single [value] (unlabelled). Useful as a fixed [combine] input. */
internal fun <T> Generator.Companion.constant(value: T): Generator<T> =
    object : Generator<T> {
        override fun representativeValues(): Sequence<GeneratorValue<T>> = sequenceOf(GeneratorValue(value = value))

        override fun arb(): Arb<T> = Arb.constant(value)
    }
