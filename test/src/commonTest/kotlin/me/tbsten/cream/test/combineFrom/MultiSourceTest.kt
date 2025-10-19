package me.tbsten.cream.test.combineFrom

import kotlin.test.Test
import kotlin.test.assertEquals

class MultiSourceTest {
    @Test
    fun combineFromFourSources() {
        val sourceA = MultiSourceA(propertyA = "A")
        val sourceB = MultiSourceB(propertyB = 42)
        val sourceC = MultiSourceC(propertyC = true)
        val sourceD = MultiSourceD(propertyD = 3.14)

        val result: MultiSourceTarget =
            sourceA.copyToMultiSourceTarget(
                multiSourceB = sourceB,
                multiSourceC = sourceC,
                multiSourceD = sourceD,
                extraProperty = "extra",
            )

        assertEquals("A", result.propertyA)
        assertEquals(42, result.propertyB)
        assertEquals(true, result.propertyC)
        assertEquals(3.14, result.propertyD)
        assertEquals("extra", result.extraProperty)
    }

    @Test
    fun combineFromFourSourcesWithOverride() {
        val sourceA = MultiSourceA(propertyA = "A")
        val sourceB = MultiSourceB(propertyB = 42)
        val sourceC = MultiSourceC(propertyC = true)
        val sourceD = MultiSourceD(propertyD = 3.14)

        val result: MultiSourceTarget =
            sourceA.copyToMultiSourceTarget(
                multiSourceB = sourceB,
                multiSourceC = sourceC,
                multiSourceD = sourceD,
                propertyA = "Overridden",
                propertyB = 100,
                propertyC = false,
                propertyD = 2.71,
                extraProperty = "extra",
            )

        assertEquals("Overridden", result.propertyA)
        assertEquals(100, result.propertyB)
        assertEquals(false, result.propertyC)
        assertEquals(2.71, result.propertyD)
        assertEquals("extra", result.extraProperty)
    }
}
