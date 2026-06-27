package me.tbsten.cream.test.combineTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class GenericsTest :
    FreeSpec({
        "combineWithGenerics" {
            val sourceA = GenericSourceA(genericProperty = 123)
            val sourceB = GenericSourceB(normalProperty = "normal")

            val result: GenericTarget<Int> =
                sourceA.copyToGenericTarget(
                    genericSourceB = sourceB,
                )

            val expected =
                GenericTarget(
                    genericProperty = 123,
                    normalProperty = "normal",
                )

            result shouldBe expected
        }
    })
