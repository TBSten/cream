package me.tbsten.cream.test.copyToChildren.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals

class CopyToChildrenEdgeCaseTest {
    @Test
    fun dataObjectToDataObject() {
        val source = EmptyChild1
        val target = source.copyToEmptyChild2()

        assertEquals(EmptyChild2, target)
    }

    @Test
    fun nestedClasses() {
        val parent = ParentClass("test", 100)
        val source = NestedChild1(parent, "test name")
        val target = source.copyToNestedChild2(value = 0)

        assertEquals(NestedChild2(parent, 0), target)
    }

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
    fun visibilityProperties() {
        val source = VisibilityChild1(publicProp = "public", internalProp = "internal")
        val target = source.copyToVisibilityChild2(newProperty = "new")

        assertEquals(
            VisibilityChild2(
                publicProp = "public",
                internalProp = "internal",
                newProperty = "new",
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
