package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MultiSourceTest :
    FunSpec({
        test("combineFromFourSources") {
            val sourceA = MultiSourceA(propertyA = "A")
            val sourceB = MultiSourceB(propertyB = 42)
            val sourceC = MultiSourceC(propertyC = true)
            val sourceD = MultiSourceD(propertyD = 3.14)

            val result: MultiSourceTarget =
                sourceA.copyToMultiSourceTarget(
                    multiSourceB = sourceB,
                    multiSourceC = sourceC,
                    multiSourceD = sourceD,
                    extraProperty = "extra",
                )

            result.propertyA shouldBe "A"
            result.propertyB shouldBe 42
            result.propertyC shouldBe true
            result.propertyD shouldBe 3.14
            result.extraProperty shouldBe "extra"
        }

        test("combineFromFourSourcesWithOverride") {
            val sourceA = MultiSourceA(propertyA = "A")
            val sourceB = MultiSourceB(propertyB = 42)
            val sourceC = MultiSourceC(propertyC = true)
            val sourceD = MultiSourceD(propertyD = 3.14)

            val result: MultiSourceTarget =
                sourceA.copyToMultiSourceTarget(
                    multiSourceB = sourceB,
                    multiSourceC = sourceC,
                    multiSourceD = sourceD,
                    propertyA = "Overridden",
                    propertyB = 100,
                    propertyC = false,
                    propertyD = 2.71,
                    extraProperty = "extra",
                )

            result.propertyA shouldBe "Overridden"
            result.propertyB shouldBe 100
            result.propertyC shouldBe false
            result.propertyD shouldBe 2.71
            result.extraProperty shouldBe "extra"
        }
    })
