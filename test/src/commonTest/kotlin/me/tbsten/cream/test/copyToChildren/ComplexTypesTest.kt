package me.tbsten.cream.test.copyToChildren

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ComplexTypesTest :
    FunSpec({
        test("complexTypes") {
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

        test("complexTypesWithNull") {
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

        test("multipleTransitions") {
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
