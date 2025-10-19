package me.tbsten.cream.test.copyToChildren

import kotlin.test.Test
import kotlin.test.assertEquals

class ComplexTypesTest {
    @Test
    fun complexTypes() {
        val source =
            ComplexTypeChild1(
                stringList = listOf("a", "b", "c"),
                intMap = mapOf("one" to 1, "two" to 2),
            )

        val target = source.copyToComplexTypeChild2()

        assertEquals(
            ComplexTypeChild2(
                stringList = listOf("a", "b", "c"),
                intMap = mapOf("one" to 1, "two" to 2),
            ),
            target,
        )
    }

    @Test
    fun complexTypesWithNull() {
        val source =
            ComplexTypeChild1(
                stringList = emptyList(),
                intMap = emptyMap(),
            )

        val target = source.copyToComplexTypeChild2()

        assertEquals(
            ComplexTypeChild2(
                stringList = emptyList(),
                intMap = emptyMap(),
            ),
            target,
        )
    }

    @Test
    fun multipleTransitions() {
        val source =
            ComplexTypeChild1(
                stringList = listOf("a", "b"),
                intMap = mapOf("x" to 1),
            )

        val intermediate = source.copyToComplexTypeChild2()
        val final =
            intermediate.copyToComplexTypeChild1(
                stringList = intermediate.stringList!!,
                intMap = intermediate.intMap!!,
            )

        assertEquals(
            ComplexTypeChild1(
                stringList = listOf("a", "b"),
                intMap = mapOf("x" to 1),
            ),
            final,
        )
    }
}
