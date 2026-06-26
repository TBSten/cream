package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ComplexTypesTest :
    FreeSpec({
        "complexTypes" {
            val source =
                ComplexTypeSource(
                    stringList = listOf("a", "b", "c"),
                    intMap = mapOf("one" to 1, "two" to 2),
                    nullableSet = setOf(1.0, 2.0, 3.0),
                )

            val target: ComplexTypeTarget = source.copyToComplexTypeTarget(newProperty = "new")

            target shouldBe
                ComplexTypeTarget(
                    stringList = listOf("a", "b", "c"),
                    intMap = mapOf("one" to 1, "two" to 2),
                    nullableSet = setOf(1.0, 2.0, 3.0),
                    newProperty = "new",
                )
        }

        "complexTypesWithNull" {
            val source =
                ComplexTypeSource(
                    stringList = listOf("a", "b", "c"),
                    intMap = mapOf("one" to 1, "two" to 2),
                    nullableSet = null,
                )

            val target: ComplexTypeTarget = source.copyToComplexTypeTarget(newProperty = "new")

            target shouldBe
                ComplexTypeTarget(
                    stringList = listOf("a", "b", "c"),
                    intMap = mapOf("one" to 1, "two" to 2),
                    nullableSet = null,
                    newProperty = "new",
                )
        }
    })
