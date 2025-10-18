package me.tbsten.cream.test.combineMapping

import kotlin.test.Test
import kotlin.test.assertEquals

class PropertyMappingTest {
    @Test
    fun combineMappingWithPropertyNames() {
        val libA = LibAModel(
            nameA = "SourceNameA",
            valueA = 42,
        )
        val libB = LibBModel(
            nameB = "SourceNameB",
            valueB = 2.71,
        )

        val result: RenamedTargetModel = libA.copyToRenamedTargetModel(
            libBModel = libB,
            extra = "Extra",
        )

        val expected = RenamedTargetModel(
            targetNameA = "SourceNameA",
            targetValueA = 42,
            targetNameB = "SourceNameB",
            targetValueB = 2.71,
            extra = "Extra",
        )

        assertEquals(expected, result)
    }

    @Test
    fun combineMappingWithPropertyNamesAndOverride() {
        val libA = LibAModel(
            nameA = "SourceNameA",
            valueA = 42,
        )
        val libB = LibBModel(
            nameB = "SourceNameB",
            valueB = 2.71,
        )

        val result: RenamedTargetModel = libA.copyToRenamedTargetModel(
            libBModel = libB,
            targetNameA = "Overridden",
            targetValueB = 999.0,
            extra = "Extra",
        )

        val expected = RenamedTargetModel(
            targetNameA = "Overridden",
            targetValueA = 42,
            targetNameB = "SourceNameB",
            targetValueB = 999.0,
            extra = "Extra",
        )

        assertEquals(expected, result)
    }
}
