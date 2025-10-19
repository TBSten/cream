package me.tbsten.cream.test.combineTo.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals

class NullableTest {
    @Test
    fun combineWithNullableProperties() {
        val sourceA = NullableSourceA(nullableProperty = "nullable value")
        val sourceB = NullableSourceB(requiredProperty = "required")

        val result: NullableTarget =
            sourceA.copyToNullableTarget(
                nullableSourceB = sourceB,
            )

        val expected =
            NullableTarget(
                nullableProperty = "nullable value",
                requiredProperty = "required",
            )

        assertEquals(expected, result)
    }

    @Test
    fun combineWithNullableNull() {
        val sourceA = NullableSourceA(nullableProperty = null)
        val sourceB = NullableSourceB(requiredProperty = "required")

        val result =
            sourceA.copyToNullableTarget(
                nullableSourceB = sourceB,
            )

        val expected =
            NullableTarget(
                nullableProperty = null,
                requiredProperty = "required",
            )

        assertEquals(expected, result)
    }
}
