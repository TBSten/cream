package me.tbsten.cream.test.combineTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class BasicTest :
    FreeSpec({
        "combineToTarget" {
            val sourceA = SourceStateA(propertyA = "sourceA")
            val sourceB = SourceStateB(propertyB = 42)

            val result: TargetState =
                sourceA.copyToTargetState(
                    sourceStateB = sourceB,
                    propertyC = true,
                )

            // Verify
            val expected =
                TargetState(
                    propertyA = "sourceA",
                    propertyB = 42,
                    propertyC = true,
                )

            result shouldBe expected
        }

        "combineToTargetWithOverride" {
            val sourceA = SourceStateA(propertyA = "sourceA")
            val sourceB = SourceStateB(propertyB = 42)

            val result: TargetState =
                sourceA.copyToTargetState(
                    sourceStateB = sourceB,
                    propertyA = "overridden",
                    propertyB = 100,
                    propertyC = true,
                )

            // Verify
            val expected =
                TargetState(
                    propertyA = "overridden",
                    propertyB = 100,
                    propertyC = true,
                )

            result shouldBe expected
        }
    })
