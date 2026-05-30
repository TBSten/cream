package me.tbsten.cream.test.combineTo

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OverlapTest :
    FunSpec({
        test("combineWithOverlappingProperties") {
            val sourceA = OverlapSourceA(sharedProperty = "from A", uniqueA = 42)
            val sourceB = OverlapSourceB(sharedProperty = "from B", uniqueB = true)

            // When called from sourceA, sourceB's sharedProperty should be prioritized
            val result: OverlapTarget =
                sourceA.copyToOverlapTarget(
                    overlapSourceB = sourceB,
                )

            val expected =
                OverlapTarget(
                    sharedProperty = "from B", // SourceB is prioritized for overlapping properties
                    uniqueA = 42,
                    uniqueB = true,
                )

            result shouldBe expected
        }

        test("combineWithOverlappingPropertiesWithOverrides") {
            val sourceA = OverlapSourceA(sharedProperty = "from A", uniqueA = 42)
            val sourceB = OverlapSourceB(sharedProperty = "from B", uniqueB = true)

            // When called from sourceA, sourceB's sharedProperty should be prioritized
            val result =
                sourceA.copyToOverlapTarget(
                    overlapSourceB = sourceB,
                    sharedProperty = "overridden",
                    uniqueA = 42,
                    uniqueB = true,
                )

            val expected =
                OverlapTarget(
                    sharedProperty = "overridden", // SourceB is prioritized for overlapping properties
                    uniqueA = 42,
                    uniqueB = true,
                )

            result shouldBe expected
        }
    })
