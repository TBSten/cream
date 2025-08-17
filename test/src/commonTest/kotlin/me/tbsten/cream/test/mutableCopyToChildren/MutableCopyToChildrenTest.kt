package me.tbsten.cream.test.mutableCopyToChildren

import kotlin.test.Test
import kotlin.test.assertEquals

class MutableCopyToChildrenTest {

    @Test
    fun testMutableCopyToChildrenDataObject() {
        // Arrange
        val source = MutableChildDataClass(
            parentProp1 = "test string",
            parentProp2 = 42,
            parentProp3 = true,
            childProp1 = 1.0,
            childProp2 = 100L,
            childProp3 = "test child",
        )
        val target = MutableChildDataObject

        // Act
        val result = source.mutableCopyToMutableChildDataObject(
            mutableTarget = target
        )

        // Assert
        // For data objects, parent properties cannot be modified, so they retain their original values
        assertEquals("aaa", result.parentProp1)
        assertEquals(1, result.parentProp2)
        assertEquals(true, result.parentProp3)
        assertEquals(target, result)
    }

    @Test
    fun testMutableCopyToChildrenDataClass() {
        // Arrange
        val source = MutableChildDataClass(
            parentProp1 = "test string",
            parentProp2 = 42,
            parentProp3 = true,
            childProp1 = 1.0,
            childProp2 = 100L,
            childProp3 = "test child",
        )
        val target = MutableChildDataClass(
            parentProp1 = "old value",
            parentProp2 = 0,
            parentProp3 = false,
            childProp1 = 1.0,
            childProp2 = 100L,
            childProp3 = "old child",
        )

        // Act
        val result = source.mutableCopyToMutableChildDataClass(
            mutableTarget = target,
            childProp1 = 2.0,
            childProp2 = 200L,
            childProp3 = "new child",
        )

        // Assert
        assertEquals("test string", result.parentProp1)
        assertEquals(42, result.parentProp2)
        assertEquals(true, result.parentProp3)
        assertEquals(2.0, result.childProp1)
        assertEquals(200L, result.childProp2)
        assertEquals("new child", result.childProp3)
        assertEquals(target, result)
    }

    @Test
    fun testMutableCopyToChildrenGrandChild() {
        // Arrange
        val source = MutableChildDataClass(
            parentProp1 = "test string",
            parentProp2 = 42,
            parentProp3 = true,
            childProp1 = 1.0,
            childProp2 = 100L,
            childProp3 = "test child",
        )
        val target = MutableChildDataClass(
            parentProp1 = "old value",
            parentProp2 = 0,
            parentProp3 = false,
            childProp1 = 1.0,
            childProp2 = 100L,
            childProp3 = "old child",
        )

        // Act
        val result = source.mutableCopyToMutableChildDataClass(
            mutableTarget = target,
            childProp1 = 2.0,
            childProp2 = 200L,
            childProp3 = "new child",
        )

        // Assert
        assertEquals("test string", result.parentProp1)
        assertEquals(42, result.parentProp2)
        assertEquals(true, result.parentProp3)
        assertEquals(2.0, result.childProp1)
        assertEquals(200L, result.childProp2)
        assertEquals("new child", result.childProp3)
        assertEquals(target, result)
    }
}
