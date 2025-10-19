package me.tbsten.cream.test.copyTo.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals

class CopyToEdgeCaseTest {
    @Test
    fun dataObjectToDataObject() {
        val source = EmptySource
        val target = source.copyToEmptyTarget()

        assertEquals(EmptyTarget, target)
    }

    @Test
    fun nonNullableToNullable() {
        val source =
            NonNullableSource(
                str = "test string",
                num = 42,
                bool = true,
                list = listOf("item1", "item2", "item3"),
            )

        val target = source.copyToNullableTarget(newProperty = "new")

        assertEquals(
            NullableTarget(
                str = "test string",
                num = 42,
                bool = true,
                list = listOf("item1", "item2", "item3"),
                newProperty = "new",
            ),
            target,
        )
    }

    @Test
    fun nonNullableToNullableWithEmptyValues() {
        val source =
            NonNullableSource(
                str = "",
                num = 0,
                bool = false,
                list = emptyList(),
            )

        val target = source.copyToNullableTarget(newProperty = "new")

        assertEquals(
            NullableTarget(
                str = "",
                num = 0,
                bool = false,
                list = emptyList(),
                newProperty = "new",
            ),
            target,
        )
    }

    @Test
    fun nestedClasses() {
        val parent = ParentClass("parent", 100)
        val source = NestedSource(parent)
        val target = source.copyToNestedTarget(newProperty = "new")

        assertEquals(NestedTarget(parent, "new"), target)
    }

    @Test
    fun complexTypes() {
        val source =
            ComplexTypeSource(
                stringList = listOf("a", "b", "c"),
                intMap = mapOf("one" to 1, "two" to 2),
                nullableSet = setOf(1.0, 2.0, 3.0),
            )

        val target = source.copyToComplexTypeTarget(newProperty = "new")

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

        val target = source.copyToComplexTypeTarget(newProperty = "new")

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

    @Test
    fun visibilityProperties() {
        val source = VisibilitySource("public", 42, true)
        val target = source.copyToVisibilityTarget(newProperty = "new")

        assertEquals(VisibilityTarget("public", "new"), target)
    }

    @Test
    fun propertyMapping() {
        val domainSource =
            DomainSourceModel(
                domainId = "test-id",
                name = "test-name",
            )

        val dataTarget = domainSource.copyToDataTargetModel()

        assertEquals(
            DataTargetModel(dataId = "test-id", name = "test-name"),
            dataTarget,
        )
    }
}
