package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class ExcludeTest {
    @Test
    fun copyToTargetWithExclude() {
        val source = CopyToExcludeTestSource(
            sharedProp = "shared",
            excludedProp = "excluded",
        )

        val result: CopyToExcludeTestTarget =
            source.copyToCopyToExcludeTestTarget(
                excludedProp = "required",
                targetProp = "target",
            )

        // Verify
        val expected =
            CopyToExcludeTestTarget(
                sharedProp = "shared",
                excludedProp = "required",
                targetProp = "target",
            )

        assertEquals(expected, result)
    }

    @Test
    fun copyToTargetWithOverride() {
        val source = CopyToExcludeTestSource(
            sharedProp = "shared",
            excludedProp = "excluded",
        )

        val result: CopyToExcludeTestTarget =
            source.copyToCopyToExcludeTestTarget(
                sharedProp = "overridden shared",
                excludedProp = "overridden excluded",
                targetProp = "target",
            )

        // Verify
        val expected =
            CopyToExcludeTestTarget(
                sharedProp = "overridden shared",
                excludedProp = "overridden excluded",
                targetProp = "target",
            )

        assertEquals(expected, result)
    }
}
