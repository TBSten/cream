package me.tbsten.cream.ksp.testing.generator.util

import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorValue

/**
 * Builds a `List<Value>` generator from an [element] generator.
 *
 * Representative values stay deterministic and minimal: the empty list, a singleton of the first
 * element value, and (when the element has more than one) a list of all element values.
 */
internal fun <Value> Generator.Companion.list(
    element: Generator<Value>,
    range: IntRange = 0..3,
): Generator<List<Value>> =
    object : Generator<List<Value>> {
        override fun representativeValues(): Sequence<GeneratorValue<List<Value>>> =
            sequence {
                val elementValues = element.representativeValues().toList()
                yield(GeneratorValue(label = "empty", value = emptyList()))
                if (elementValues.isNotEmpty()) {
                    yield(GeneratorValue(label = "single", value = listOf(elementValues.first().value)))
                }
                if (elementValues.size > 1) {
                    yield(GeneratorValue(label = "all", value = elementValues.map { it.value }))
                }
            }

        override fun arb(): Arb<List<Value>> = Arb.list(element.arb(), range)
    }
