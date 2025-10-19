package me.tbsten.cream.test.combineFrom.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NullableTest {
    @Test
    fun combineFromWithNullableProperties() {
        val sourceA =
            NullableSourceA(
                nullableProperty = "value",
                nonNullProperty = "required",
            )
        val sourceB = NullableSourceB(anotherNullable = 42)

        val result: NullableTarget =
            sourceA.copyToNullableTarget(
                nullableSourceB = sourceB,
                extraProperty = true,
            )

        assertEquals("value", result.nullableProperty)
        assertEquals("required", result.nonNullProperty)
        assertEquals(42, result.anotherNullable)
        assertEquals(true, result.extraProperty)
    }

    @Test
    fun combineFromWithNullValues() {
        val sourceA =
            NullableSourceA(
                nullableProperty = null,
                nonNullProperty = "required",
            )
        val sourceB = NullableSourceB(anotherNullable = null)

        val result: NullableTarget =
            sourceA.copyToNullableTarget(
                nullableSourceB = sourceB,
                extraProperty = false,
            )

        assertNull(result.nullableProperty)
        assertEquals("required", result.nonNullProperty)
        assertNull(result.anotherNullable)
        assertEquals(false, result.extraProperty)
    }
}
