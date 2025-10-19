package me.tbsten.cream.test.combineTo

import kotlin.test.Test
import kotlin.test.assertEquals

class GenericsTest {
    @Test
    fun combineWithGenerics() {
        val sourceA = GenericSourceA(genericProperty = 123)
        val sourceB = GenericSourceB(normalProperty = "normal")

        val result: GenericTarget<Int> =
            sourceA.copyToGenericTarget(
                genericSourceB = sourceB,
            )

        val expected =
            GenericTarget(
                genericProperty = 123,
                normalProperty = "normal",
            )

        assertEquals(expected, result)
    }
}
