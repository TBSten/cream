package me.tbsten.cream.ksp.testing.generator

import io.kotest.property.Arb

/**
 * DSL entry point for building a [Generator]:
 * ```
 * val ids = generator {
 *     "Basic" case 123     // labelled representative value
 *     "Zero" case 0
 *     case(456)            // unlabelled representative value
 *     Arb.int()            // the block's return value is the Arb
 * }
 * ```
 * The lambda registers representative values via [GeneratorBuilder.case] (and its labelled infix
 * sugar) and returns the [Arb] used by property tests.
 */
internal fun <Value> generator(builder: GeneratorBuilder<Value>.() -> Arb<Value>): Generator<Value> {
    val scope = GeneratorBuilderImpl<Value>()
    val arb = scope.builder()
    return object : Generator<Value> {
        override fun representativeValues(): Sequence<GeneratorValue<Value>> = scope.collected.toList().asSequence()

        override fun arb(): Arb<Value> = arb
    }
}

/** Receiver of the [generator] DSL lambda; registers representative values, optionally labelled. */
internal interface GeneratorBuilder<Value> {
    /** Registers an unlabelled representative value: `case(123)`. Use the infix form for a label. */
    fun case(value: Value)

    /** Labelled sugar so the label reads first: `"Basic" case 123`. */
    infix fun String.case(value: Value)
}

private class GeneratorBuilderImpl<Value> : GeneratorBuilder<Value> {
    val collected = mutableListOf<GeneratorValue<Value>>()

    override fun case(value: Value) {
        collected += GeneratorValue(label = null, value = value)
    }

    override infix fun String.case(value: Value) {
        collected += GeneratorValue(label = this, value = value)
    }
}
