package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BasicTest :
    FunSpec({
        test("combineFromTarget") {
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

        test("combineFromTargetWithOverride") {
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
