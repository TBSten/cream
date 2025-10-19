package me.tbsten.cream.test.combineFrom

import kotlin.test.Test
import kotlin.test.assertEquals

class ExcludeTest {
    @Test
    fun combineFromTarget() {
        val sourceA = ExcludeTestSourceA(sourceAProperty = "sourceA")
        val sourceB = ExcludeTestSourceB(sourceBProperty = "sourceB")

        val result: ExcludeTestTarget =
            sourceA.copyToExcludeTestTarget(
                sourceB = sourceB,
                sourceBProperty = "overriden",
            )

        // Verify
        val expected =
            ExcludeTestTarget(
                sourceAProperty = "sourceA",
                sourceBProperty = "overriden",
            )

        assertEquals(expected, result)
    }

    @Test
    fun combineFromTargetWithOverride() {
        val sourceA = ExcludeTestSourceA(sourceAProperty = "sourceA")
        val sourceB = ExcludeTestSourceB(sourceBProperty = "sourceB")

        val result: ExcludeTestTarget =
            sourceA.copyToExcludeTestTarget(
                sourceB = sourceB,
                sourceAProperty = "overriden A",
                sourceBProperty = "overriden B",
            )

        // Verify
        val expected: ExcludeTestTarget =
            ExcludeTestTarget(
                sourceAProperty = "overriden A",
                sourceBProperty = "overriden B",
            )

        assertEquals(expected, result)
    }
}
