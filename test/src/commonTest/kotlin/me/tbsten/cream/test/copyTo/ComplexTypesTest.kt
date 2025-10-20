package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class ComplexTypesTest {
    @Test
    fun complexTypes() {
        val source =
            ComplexTypeSource(
                stringList = listOf("a", "b", "c"),
                intMap = mapOf("one" to 1, "two" to 2),
                nullableSet = setOf(1.0, 2.0, 3.0),
            )

        val target: ComplexTypeTarget = source.copyToComplexTypeTarget(newProperty = "new")

        assertEquals(
            ComplexTypeTarget(
                stringList = listOf("a", "b", "c"),
                intMap = mapOf("one" to 1, "two" to 2),
                nullableSet = setOf(1.0, 2.0, 3.0),
                newProperty = "new",
            ),
            target,
        )
    }

    @Test
    fun complexTypesWithNull() {
        val source =
            ComplexTypeSource(
                stringList = listOf("a", "b", "c"),
                intMap = mapOf("one" to 1, "two" to 2),
                nullableSet = null,
            )

        val target: ComplexTypeTarget = source.copyToComplexTypeTarget(newProperty = "new")

        assertEquals(
            ComplexTypeTarget(
                stringList = listOf("a", "b", "c"),
                intMap = mapOf("one" to 1, "two" to 2),
                nullableSet = null,
                newProperty = "new",
            ),
            target,
        )
    }
}
