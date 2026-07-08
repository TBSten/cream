package me.tbsten.cream.test.copyMapping

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ExcludeTest :
    FreeSpec({
        "excludedParameterMustBePassedExplicitly" {
            val source =
                ExcludeSourceModel(
                    shareProp = "shared",
                    sourceProp = 1,
                )

            // shareProp has no auto-copy default (excludes = ["shareProp"]), so it must be passed.
            val result =
                source.copyToExcludeTargetModel(
                    shareProp = "explicit",
                    targetProp = 2,
                )

            val expected =
                ExcludeTargetModel(
                    shareProp = "explicit",
                    targetProp = 2,
                )

            result shouldBe expected
        }

        "excludedMappedParameterInForwardFunction" {
            val source =
                ExcludeReverseSource(
                    mappedSource = "value",
                    shared = "shared",
                )

            // mappedTarget (the target-side name of the Map) is required; shared keeps its default.
            val result =
                source.copyToExcludeReverseTarget(
                    mappedTarget = "explicit",
                )

            val expected =
                ExcludeReverseTarget(
                    mappedTarget = "explicit",
                    shared = "shared",
                )

            result shouldBe expected
        }

        "excludedTranslatedParameterInReverseFunction" {
            val target =
                ExcludeReverseTarget(
                    mappedTarget = "value",
                    shared = "shared",
                )

            // In the reverse function the exclude entry "mappedTarget" is translated through the
            // reversed Map, so mappedSource is the required parameter.
            val result =
                target.copyToExcludeReverseSource(
                    mappedSource = "explicit",
                )

            val expected =
                ExcludeReverseSource(
                    mappedSource = "explicit",
                    shared = "shared",
                )

            result shouldBe expected
        }
    })
