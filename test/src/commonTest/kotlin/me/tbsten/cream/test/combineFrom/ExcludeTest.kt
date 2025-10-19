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
                excludeTestSourceB = sourceB,
                sourceBProperty = "overridden",
            )

        // Verify
        val expected =
            ExcludeTestTarget(
                sourceAProperty = "sourceA",
                sourceBProperty = "overridden",
            )

        assertEquals(expected, result)
    }

    @Test
    fun combineFromTargetWithOverride() {
        val sourceA = ExcludeTestSourceA(sourceAProperty = "sourceA")
        val sourceB = ExcludeTestSourceB(sourceBProperty = "sourceB")

        val result: ExcludeTestTarget =
            sourceA.copyToExcludeTestTarget(
                excludeTestSourceB = sourceB,
                sourceAProperty = "overridden A",
                sourceBProperty = "overridden B",
            )

        // Verify
        val expected: ExcludeTestTarget =
            ExcludeTestTarget(
                sourceAProperty = "overridden A",
                sourceBProperty = "overridden B",
            )

        assertEquals(expected, result)
    }
}
