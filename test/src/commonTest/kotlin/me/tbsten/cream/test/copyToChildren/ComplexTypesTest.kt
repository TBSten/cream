package me.tbsten.cream.test.copyToChildren

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ComplexTypesTest :
    FreeSpec({
        "complexTypes" {
            val source =
                ComplexTypeChild1(
                    stringList = listOf("a", "b", "c"),
                    intMap = mapOf("one" to 1, "two" to 2),
                )

            val target: ComplexTypeChild2 = source.copyToComplexTypeChild2()

            target shouldBe
                ComplexTypeChild2(
                    stringList = listOf("a", "b", "c"),
                    intMap = mapOf("one" to 1, "two" to 2),
                )
        }

        "complexTypesWithNull" {
            val source =
                ComplexTypeChild1(
                    stringList = emptyList(),
                    intMap = emptyMap(),
                )

            val target: ComplexTypeChild2 = source.copyToComplexTypeChild2()

            target shouldBe
                ComplexTypeChild2(
                    stringList = emptyList(),
                    intMap = emptyMap(),
                )
        }

        "multipleTransitions" {
            val source =
                ComplexTypeChild1(
                    stringList = listOf("a", "b"),
                    intMap = mapOf("x" to 1),
                )

            val intermediate: ComplexTypeChild2 = source.copyToComplexTypeChild2()
            val final =
                intermediate.copyToComplexTypeChild1(
                    stringList = intermediate.stringList!!,
                    intMap = intermediate.intMap!!,
                )

            final shouldBe
                ComplexTypeChild1(
                    stringList = listOf("a", "b"),
                    intMap = mapOf("x" to 1),
                )
        }
    })
