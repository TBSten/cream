package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class OverlapTest :
    FreeSpec({
        "overlappingPropertyPriority" {
            val sourceA =
                OverlapSourceA(
                    shared = "from A",
                    uniqueA = "unique A",
                )
            val sourceB =
                OverlapSourceB(
                    shared = "from B",
                    uniqueB = 42,
                )

            val result: OverlapTarget =
                sourceA.copyToOverlapTarget(
                    overlapSourceB = sourceB,
                )

            // The last source class (SourceB) should take precedence for 'shared'
            result.shared shouldBe "from B"
            result.uniqueA shouldBe "unique A"
            result.uniqueB shouldBe 42
        }

        "overlappingPropertyWithExplicitOverride" {
            val sourceA =
                OverlapSourceA(
                    shared = "from A",
                    uniqueA = "unique A",
                )
            val sourceB =
                OverlapSourceB(
                    shared = "from B",
                    uniqueB = 42,
                )

            val result: OverlapTarget =
                sourceA.copyToOverlapTarget(
                    overlapSourceB = sourceB,
                    shared = "explicitly set",
                )

            // Explicit parameter should override both sources
            result.shared shouldBe "explicitly set"
            result.uniqueA shouldBe "unique A"
            result.uniqueB shouldBe 42
        }
    })
