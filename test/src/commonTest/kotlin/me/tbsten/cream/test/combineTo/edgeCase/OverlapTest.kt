package me.tbsten.cream.test.combineTo.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals

class OverlapTest {
    @Test
    fun combineWithOverlappingProperties() {
        val sourceA = OverlapSourceA(sharedProperty = "from A", uniqueA = 42)
        val sourceB = OverlapSourceB(sharedProperty = "from B", uniqueB = true)

        // When called from sourceA, sourceB's sharedProperty should be prioritized
        val result: OverlapTarget =
            sourceA.copyToOverlapTarget(
                overlapSourceB = sourceB,
            )

        val expected =
            OverlapTarget(
                sharedProperty = "from B", // SourceB is prioritized for overlapping properties
                uniqueA = 42,
                uniqueB = true,
            )

        assertEquals(expected, result)
    }

    @Test
    fun combineWithOverlappingPropertiesWithOverrides() {
        val sourceA = OverlapSourceA(sharedProperty = "from A", uniqueA = 42)
        val sourceB = OverlapSourceB(sharedProperty = "from B", uniqueB = true)

        // When called from sourceA, sourceB's sharedProperty should be prioritized
        val result =
            sourceA.copyToOverlapTarget(
                overlapSourceB = sourceB,
                sharedProperty = "overridden",
                uniqueA = 42,
                uniqueB = true,
            )

        val expected =
            OverlapTarget(
                sharedProperty = "overridden", // SourceB is prioritized for overlapping properties
                uniqueA = 42,
                uniqueB = true,
            )

        assertEquals(expected, result)
    }
}
