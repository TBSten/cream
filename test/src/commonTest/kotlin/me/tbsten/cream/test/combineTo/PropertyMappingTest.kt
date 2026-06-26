package me.tbsten.cream.test.combineTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class PropertyMappingTest :
    FreeSpec({
        "simplePropertyNameMapping" {
            val sourceA = MappingSourceA(sourcePropertyA = "Hello")
            val sourceB = MappingSourceB(sourcePropertyB = 42)

            val result: MappedTarget =
                sourceA.copyToMappedTarget(
                    mappingSourceB = sourceB,
                    normalProperty = "World",
                )

            result.targetPropertyA shouldBe "Hello"
            result.targetPropertyB shouldBe 42
            result.normalProperty shouldBe "World"
        }

        "multiplePropertyNamesMapping" {
            val source = MultiMappingSource(sourceName = "SharedValue")
            val sourceB = MultiMappingSourceB(otherProp = 100)

            val result: MultiMappedTarget =
                source.copyToMultiMappedTarget(
                    multiMappingSourceB = sourceB,
                )

            result.targetName1 shouldBe "SharedValue"
            result.targetName2 shouldBe "SharedValue"
            result.otherProp shouldBe 100
        }

        "mixedMappingWithDirectMatch" {
            val sourceA = MixedMappingSourceA(directMatch = "Direct")
            val sourceB = MixedMappingSourceB(originalProperty = 999)

            val result: MixedMappingTarget =
                sourceA.copyToMixedMappingTarget(
                    mixedMappingSourceB = sourceB,
                    extraProperty = true,
                )

            result.directMatch shouldBe "Direct"
            result.renamedProperty shouldBe 999
            result.extraProperty shouldBe true
        }

        "mergeMappingWithDirectMatch" {
            val sourceA = MergeMappingSourceA(sourcePropertyA = "SourceA")
            val sourceB = MergeMappingSourceB(sourcePropertyB = "SourceB")

            val result =
                sourceA.copyToMergeMappingTarget(
                    mergeMappingSourceB = sourceB,
                )

            result.sourcePropertyA shouldBe "SourceB"
        }
    })
