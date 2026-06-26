package me.tbsten.cream.ksp.testing.generator

import io.kotest.property.Arb

/**
 * Wraps an existing [Arb] together with an explicit sequence of representative values.
 *
 * `Arb.string().toGenerator(sequenceOf("", "a"))`
 */
internal fun <Value> Arb<Value>.toGenerator(representativeValues: Sequence<Value>): Generator<Value> {
    val arb = this
    return generator {
        representativeValues.forEach { case(it) }
        arb
    }
}

/** vararg convenience for [toGenerator]: `Arb.string().toGenerator("", "a")`. */
internal fun <Value> Arb<Value>.toGenerator(vararg representativeValues: Value): Generator<Value> = toGenerator(representativeValues.asSequence())
