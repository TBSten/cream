package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class TypeAliasTest :
    FreeSpec({
        "testCombineFromWithTypeAliasTarget" {
            // Create instances using actual class constructors and assign them to variables with typealias types.
            // Note: Typealiases are just alternative names for the same types.
            val sourceX: SourceXAlias = SourceX(propX = "x-value")
            val sourceY: SourceYAlias = SourceY(propY = 99)

            // Call the generated combine function
            // Note: Function name and parameter names are based on the actual type (CombinedResult, SourceY), not the alias
            val result: CombinedResultAlias =
                sourceX.copyToCombinedResult(
                    sourceY = sourceY,
                    extraProp = "extra",
                )

            // Verify all properties are copied correctly
            result.propX shouldBe "x-value"
            result.propY shouldBe 99
            result.extraProp shouldBe "extra"

            // Verify the result is actually a CombinedResult instance
            result.shouldBeInstanceOf<CombinedResult>()
        }

        "testCombineFromPreservesAllSourceProperties" {
            val sourceX: SourceXAlias = SourceX(propX = "test-x")
            val sourceY: SourceYAlias = SourceY(propY = 200)

            val result: CombinedResultAlias =
                sourceX.copyToCombinedResult(
                    sourceY = sourceY,
                    extraProp = "test-extra",
                )

            // Verify that all source properties are preserved in the result
            result.propX shouldBe sourceX.propX
            result.propY shouldBe sourceY.propY
        }
    })
