package me.tbsten.cream.test.copyMapping

import kotlin.test.Test
import kotlin.test.assertEquals

class ExcludeTest {
    @Test
    fun copyMappingWithExclude() {
        val source = CopyMappingExcludeTestSource(
            sharedProp = "shared",
            excludedProp = "excluded",
        )

        val result: CopyMappingExcludeTestTarget =
            source.copyToCopyMappingExcludeTestTarget(
                excludedProp = "required",
                targetProp = "target",
            )

        // Verify
        val expected =
            CopyMappingExcludeTestTarget(
                sharedProp = "shared",
                excludedProp = "required",
                targetProp = "target",
            )

        assertEquals(expected, result)
    }

    @Test
    fun copyMappingWithOverride() {
        val source = CopyMappingExcludeTestSource(
            sharedProp = "shared",
            excludedProp = "excluded",
        )

        val result: CopyMappingExcludeTestTarget =
            source.copyToCopyMappingExcludeTestTarget(
                sharedProp = "overridden shared",
                excludedProp = "overridden excluded",
                targetProp = "target",
            )

        // Verify
        val expected =
            CopyMappingExcludeTestTarget(
                sharedProp = "overridden shared",
                excludedProp = "overridden excluded",
                targetProp = "target",
            )

        assertEquals(expected, result)
    }
}
