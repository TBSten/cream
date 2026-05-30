package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GenericsTest :
    FunSpec({
        test("combineFromWithGenerics") {
            val sourceA =
                GenericSourceA(
                    genericProperty = "generic string",
                    normalProperty = "normal",
                )
            val sourceB =
                GenericSourceB(
                    anotherGeneric = 42,
                )

            val result: GenericTarget<String, Int> =
                sourceA.copyToGenericTarget(
                    genericSourceB = sourceB,
                    extraProperty = 100,
                )

            result.genericProperty shouldBe "generic string"
            result.normalProperty shouldBe "normal"
            result.anotherGeneric shouldBe 42
            result.extraProperty shouldBe 100
        }

        test("combineFromWithComplexGenerics") {
            val sourceA =
                GenericSourceA(
                    genericProperty = listOf("a", "b", "c"),
                    normalProperty = "normal",
                )
            val sourceB =
                GenericSourceB(
                    anotherGeneric = mapOf("key" to "value"),
                )

            val result: GenericTarget<List<String>, Map<String, String>> =
                sourceA.copyToGenericTarget(
                    genericSourceB = sourceB,
                    extraProperty = 200,
                )

            result.genericProperty shouldBe listOf("a", "b", "c")
            result.normalProperty shouldBe "normal"
            result.anotherGeneric shouldBe mapOf("key" to "value")
            result.extraProperty shouldBe 200
        }
    })
