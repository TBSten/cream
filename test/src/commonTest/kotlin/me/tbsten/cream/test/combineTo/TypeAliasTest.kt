package me.tbsten.cream.test.combineTo

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class TypeAliasTest :
    FunSpec({
        test("testCombineToWithTypeAliasSource") {
            // Create instances using actual class constructors and assign them to variables with typealias types.
            // Note: Typealiases are just alternative names for the same types.
            val sourceA: SourceAWithAlias = SourceA(propA = "value-a")
            val sourceB: SourceBWithAlias = SourceB(propB = 42)

            // Call the generated combine function
            // Note: Function name and parameter names are based on the actual type (CombinedTarget, SourceB), not the alias
            val combined: CombinedTargetAlias =
                sourceA.copyToCombinedTarget(
                    sourceB = sourceB,
                    extra = "extra-value",
                )

            // Verify all properties are copied correctly
            combined.propA shouldBe "value-a"
            combined.propB shouldBe 42
            combined.extra shouldBe "extra-value"

            // Verify the result is actually a CombinedTarget instance
            combined.shouldBeInstanceOf<CombinedTarget>()
        }

        test("testCombineToPreservesAllProperties") {
            val sourceA: SourceAWithAlias = SourceA(propA = "test-prop-a")
            val sourceB: SourceBWithAlias = SourceB(propB = 100)

            val combined: CombinedTargetAlias =
                sourceA.copyToCombinedTarget(
                    sourceB = sourceB,
                    extra = "test-extra",
                )

            // Verify that all source properties are preserved
            combined.propA shouldBe sourceA.propA
            combined.propB shouldBe sourceB.propB
        }
    })
