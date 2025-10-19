package me.tbsten.cream.test.combineMapping

import kotlin.test.Test
import kotlin.test.assertEquals

class ExcludeTest {
    @Test
    fun combineMappingWithExclude() {
        val sourceA =
            CombineMappingExcludeTestSourceA(
                sourceAProp = "sourceA",
                excludedProp = "excluded",
            )
        val sourceB =
            CombineMappingExcludeTestSourceB(
                sourceBProp = "sourceB",
            )

        val result: CombineMappingExcludeTestTarget =
            sourceA.copyToCombineMappingExcludeTestTarget(
                combineMappingExcludeTestSourceB = sourceB,
                excludedProp = "required",
            )

        // Verify
        val expected =
            CombineMappingExcludeTestTarget(
                sourceAProp = "sourceA",
                excludedProp = "required",
                sourceBProp = "sourceB",
            )

        assertEquals(expected, result)
    }

    @Test
    fun combineMappingWithOverride() {
        val sourceA =
            CombineMappingExcludeTestSourceA(
                sourceAProp = "sourceA",
                excludedProp = "excluded",
            )
        val sourceB =
            CombineMappingExcludeTestSourceB(
                sourceBProp = "sourceB",
            )

        val result: CombineMappingExcludeTestTarget =
            sourceA.copyToCombineMappingExcludeTestTarget(
                combineMappingExcludeTestSourceB = sourceB,
                sourceAProp = "overridden A",
                excludedProp = "overridden excluded",
                sourceBProp = "overridden B",
            )

        // Verify
        val expected =
            CombineMappingExcludeTestTarget(
                sourceAProp = "overridden A",
                excludedProp = "overridden excluded",
                sourceBProp = "overridden B",
            )

        assertEquals(expected, result)
    }
}
