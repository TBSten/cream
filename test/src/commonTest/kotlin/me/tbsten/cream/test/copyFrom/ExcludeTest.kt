package me.tbsten.cream.test.copyFrom

import kotlin.test.Test
import kotlin.test.assertEquals

class ExcludeTest {
    @Test
    fun copyFromSourceWithExclude() {
        val source =
            CopyFromExcludeTestSource(
                sharedProp = "shared",
                sourceProp = "source",
            )

        val result: CopyFromExcludeTestTarget =
            source.copyToCopyFromExcludeTestTarget(
                sourceProp = "required",
                targetProp = "target",
            )

        // Verify
        val expected =
            CopyFromExcludeTestTarget(
                sharedProp = "shared",
                sourceProp = "required",
                targetProp = "target",
            )

        assertEquals(expected, result)
    }

    @Test
    fun copyFromSourceWithOverride() {
        val source =
            CopyFromExcludeTestSource(
                sharedProp = "shared",
                sourceProp = "source",
            )

        val result: CopyFromExcludeTestTarget =
            source.copyToCopyFromExcludeTestTarget(
                sharedProp = "overridden shared",
                sourceProp = "overridden source",
                targetProp = "target",
            )

        // Verify
        val expected =
            CopyFromExcludeTestTarget(
                sharedProp = "overridden shared",
                sourceProp = "overridden source",
                targetProp = "target",
            )

        assertEquals(expected, result)
    }
}
