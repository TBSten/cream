package me.tbsten.cream.test.combineMapping

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ExcludeTest :
    FreeSpec({
        "excludedParameterMustBePassedExplicitly" {
            val partA =
                ExcludePartA(
                    shareProp = "shared",
                    aProp = 1,
                )
            val partB =
                ExcludePartB(
                    bProp = true,
                )

            // shareProp has no auto-copy default (excludes = ["shareProp"]), so it must be passed.
            val result =
                partA.copyToExcludeCombined(
                    excludePartB = partB,
                    shareProp = "explicit",
                )

            val expected =
                ExcludeCombined(
                    shareProp = "explicit",
                    aProp = 1,
                    bProp = true,
                )

            result shouldBe expected
        }
    })
