package me.tbsten.cream.test.combineFrom.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals

class OverlapTest {
    @Test
    fun overlappingPropertyPriority() {
        val sourceA =
            OverlapSourceA(
                shared = "from A",
                uniqueA = "unique A",
            )
        val sourceB =
            OverlapSourceB(
                shared = "from B",
                uniqueB = 42,
            )

        val result: OverlapTarget =
            sourceA.copyToOverlapTarget(
                overlapSourceB = sourceB,
            )

        // The last source class (SourceB) should take precedence for 'shared'
        assertEquals("from B", result.shared)
        assertEquals("unique A", result.uniqueA)
        assertEquals(42, result.uniqueB)
    }

    @Test
    fun overlappingPropertyWithExplicitOverride() {
        val sourceA =
            OverlapSourceA(
                shared = "from A",
                uniqueA = "unique A",
            )
        val sourceB =
            OverlapSourceB(
                shared = "from B",
                uniqueB = 42,
            )

        val result: OverlapTarget =
            sourceA.copyToOverlapTarget(
                overlapSourceB = sourceB,
                shared = "explicitly set",
            )

        // Explicit parameter should override both sources
        assertEquals("explicitly set", result.shared)
        assertEquals("unique A", result.uniqueA)
        assertEquals(42, result.uniqueB)
    }
}
