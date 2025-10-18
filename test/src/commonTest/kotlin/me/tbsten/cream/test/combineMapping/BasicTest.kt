package me.tbsten.cream.test.combineMapping

import kotlin.test.Test
import kotlin.test.assertEquals

class BasicTest {
    @Test
    fun basicCombineMapping() {
        val libA = LibAModel(
            nameA = "NameA",
            valueA = 100,
        )
        val libB = LibBModel(
            nameB = "NameB",
            valueB = 3.14,
        )

        val result: CombinedModel = libA.copyToCombinedModel(
            libBModel = libB,
            extraProperty = "Extra",
        )

        val expected = CombinedModel(
            nameA = "NameA",
            valueA = 100,
            nameB = "NameB",
            valueB = 3.14,
            extraProperty = "Extra",
        )

        assertEquals(expected, result)
    }

    @Test
    fun basicCombineMappingWithOverride() {
        val libA = LibAModel(
            nameA = "NameA",
            valueA = 100,
        )
        val libB = LibBModel(
            nameB = "NameB",
            valueB = 3.14,
        )

        val result: CombinedModel = libA.copyToCombinedModel(
            libBModel = libB,
            nameA = "OverriddenNameA",
            valueB = 9.99,
            extraProperty = "Extra",
        )

        val expected = CombinedModel(
            nameA = "OverriddenNameA",
            valueA = 100,
            nameB = "NameB",
            valueB = 9.99,
            extraProperty = "Extra",
        )

        assertEquals(expected, result)
    }
}
