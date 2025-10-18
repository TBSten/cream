package me.tbsten.cream.test.combineTo.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals

class MultiSourceTest {
    @Test
    fun combineMultipleSources() {
        val sourceA = MultiSourceA(propertyA = "A")
        val sourceB = MultiSourceB(propertyB = 1)
        val sourceC = MultiSourceC(propertyC = true)
        val sourceD = MultiSourceD(propertyD = 3.14)

        val result = sourceA.copyToMultiSourceTarget(
            multiSourceB = sourceB,
            multiSourceC = sourceC,
            multiSourceD = sourceD,
        )

        val expected = MultiSourceTarget(
            propertyA = "A",
            propertyB = 1,
            propertyC = true,
            propertyD = 3.14,
        )

        assertEquals(expected, result)
    }
}
