package me.tbsten.cream.test.combineFrom.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals

class GenericsTest {
    @Test
    fun combineFromWithGenerics() {
        val sourceA = GenericSourceA(
            genericProperty = "generic string",
            normalProperty = "normal",
        )
        val sourceB = GenericSourceB(
            anotherGeneric = 42,
        )

        val result: GenericTarget<String, Int> = sourceA.copyToGenericTarget(
            genericSourceB = sourceB,
            extraProperty = 100,
        )

        assertEquals("generic string", result.genericProperty)
        assertEquals("normal", result.normalProperty)
        assertEquals(42, result.anotherGeneric)
        assertEquals(100, result.extraProperty)
    }

    @Test
    fun combineFromWithComplexGenerics() {
        val sourceA = GenericSourceA(
            genericProperty = listOf("a", "b", "c"),
            normalProperty = "normal",
        )
        val sourceB = GenericSourceB(
            anotherGeneric = mapOf("key" to "value"),
        )

        val result: GenericTarget<List<String>, Map<String, String>> = sourceA.copyToGenericTarget(
            genericSourceB = sourceB,
            extraProperty = 200,
        )

        assertEquals(listOf("a", "b", "c"), result.genericProperty)
        assertEquals("normal", result.normalProperty)
        assertEquals(mapOf("key" to "value"), result.anotherGeneric)
        assertEquals(200, result.extraProperty)
    }
}
