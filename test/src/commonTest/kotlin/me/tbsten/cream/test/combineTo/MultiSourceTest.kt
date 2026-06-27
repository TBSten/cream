package me.tbsten.cream.test.combineTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class MultiSourceTest :
    FreeSpec({
        "combineMultipleSources" {
            val sourceA = MultiSourceA(propertyA = "A")
            val sourceB = MultiSourceB(propertyB = 1)
            val sourceC = MultiSourceC(propertyC = true)
            val sourceD = MultiSourceD(propertyD = 3.14)

            val result =
                sourceA.copyToMultiSourceTarget(
                    multiSourceB = sourceB,
                    multiSourceC = sourceC,
                    multiSourceD = sourceD,
                )

            val expected =
                MultiSourceTarget(
                    propertyA = "A",
                    propertyB = 1,
                    propertyC = true,
                    propertyD = 3.14,
                )

            result shouldBe expected
        }
    })
