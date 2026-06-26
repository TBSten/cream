package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class NullableTest :
    FreeSpec({
        "combineFromWithNullableProperties" {
            val sourceA =
                NullableSourceA(
                    nullableProperty = "value",
                    nonNullProperty = "required",
                )
            val sourceB = NullableSourceB(anotherNullable = 42)

            val result: NullableTarget =
                sourceA.copyToNullableTarget(
                    nullableSourceB = sourceB,
                    extraProperty = true,
                )

            result.nullableProperty shouldBe "value"
            result.nonNullProperty shouldBe "required"
            result.anotherNullable shouldBe 42
            result.extraProperty shouldBe true
        }

        "combineFromWithNullValues" {
            val sourceA =
                NullableSourceA(
                    nullableProperty = null,
                    nonNullProperty = "required",
                )
            val sourceB = NullableSourceB(anotherNullable = null)

            val result: NullableTarget =
                sourceA.copyToNullableTarget(
                    nullableSourceB = sourceB,
                    extraProperty = false,
                )

            result.nullableProperty shouldBe null
            result.nonNullProperty shouldBe "required"
            result.anotherNullable shouldBe null
            result.extraProperty shouldBe false
        }
    })
