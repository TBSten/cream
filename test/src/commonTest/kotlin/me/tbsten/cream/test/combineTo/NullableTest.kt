package me.tbsten.cream.test.combineTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class NullableTest :
    FreeSpec({
        "combineWithNullableProperties" {
            val sourceA = NullableSourceA(nullableProperty = "nullable value")
            val sourceB = NullableSourceB(requiredProperty = "required")

            val result: NullableTarget =
                sourceA.copyToNullableTarget(
                    nullableSourceB = sourceB,
                )

            val expected =
                NullableTarget(
                    nullableProperty = "nullable value",
                    requiredProperty = "required",
                )

            result shouldBe expected
        }

        "combineWithNullableNull" {
            val sourceA = NullableSourceA(nullableProperty = null)
            val sourceB = NullableSourceB(requiredProperty = "required")

            val result =
                sourceA.copyToNullableTarget(
                    nullableSourceB = sourceB,
                )

            val expected =
                NullableTarget(
                    nullableProperty = null,
                    requiredProperty = "required",
                )

            result shouldBe expected
        }
    })
