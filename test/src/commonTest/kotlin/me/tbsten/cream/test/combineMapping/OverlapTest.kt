package me.tbsten.cream.test.combineMapping

import kotlin.test.Test
import kotlin.test.assertEquals

class OverlapTest {
    @Test
    fun combineMappingWithOverlap() {
        val libD = LibDModel(
            sharedName = "FromD",
            specificD = "ValueD",
        )
        val libE = LibEModel(
            sharedName = "FromE",  // This should take precedence
            specificE = 100,
        )

        val result: OverlapTargetModel = libD.copyToOverlapTargetModel(
            libEModel = libE,
        )

        val expected = OverlapTargetModel(
            sharedName = "FromE",  // Last source (LibE) takes precedence
            valueA = "ValueD",      // Mapped from specificD
            valueB = 100,           // Mapped from specificE
        )

        assertEquals(expected, result)
    }

    @Test
    fun combineMappingWithOverlapAndOverride() {
        val libD = LibDModel(
            sharedName = "FromD",
            specificD = "ValueD",
        )
        val libE = LibEModel(
            sharedName = "FromE",
            specificE = 100,
        )

        val result: OverlapTargetModel = libD.copyToOverlapTargetModel(
            libEModel = libE,
            sharedName = "Overridden",
        )

        val expected = OverlapTargetModel(
            sharedName = "Overridden",
            valueA = "ValueD",
            valueB = 100,
        )

        assertEquals(expected, result)
    }
}
