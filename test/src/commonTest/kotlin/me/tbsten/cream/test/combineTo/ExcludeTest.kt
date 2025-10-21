package me.tbsten.cream.test.combineTo

import kotlin.test.Test
import kotlin.test.assertEquals

class ExcludeTest {
    @Test
    fun combineToTargetWithExclude() {
        val sourceA =
            CombineToExcludeTestSourceA(
                sourceAProp = "sourceA",
                excludedProp = "excluded",
            )
        val sourceB =
            CombineToExcludeTestSourceB(
                sourceBProp = "sourceB",
            )

        val result: CombineToExcludeTestTarget =
            sourceA.copyToCombineToExcludeTestTarget(
                combineToExcludeTestSourceB = sourceB,
                excludedProp = "required",
            )

        // Verify
        val expected =
            CombineToExcludeTestTarget(
                sourceAProp = "sourceA",
                excludedProp = "required",
                sourceBProp = "sourceB",
            )

        assertEquals(expected, result)
    }

    @Test
    fun combineToTargetWithOverride() {
        val sourceA =
            CombineToExcludeTestSourceA(
                sourceAProp = "sourceA",
                excludedProp = "excluded",
            )
        val sourceB =
            CombineToExcludeTestSourceB(
                sourceBProp = "sourceB",
            )

        val result: CombineToExcludeTestTarget =
            sourceA.copyToCombineToExcludeTestTarget(
                combineToExcludeTestSourceB = sourceB,
                sourceAProp = "overridden A",
                excludedProp = "overridden excluded",
                sourceBProp = "overridden B",
            )

        // Verify
        val expected =
            CombineToExcludeTestTarget(
                sourceAProp = "overridden A",
                excludedProp = "overridden excluded",
                sourceBProp = "overridden B",
            )

        assertEquals(expected, result)
    }
}
