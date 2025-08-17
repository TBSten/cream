package me.tbsten.cream.test.mutableCopyTo.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MutableCopyToEdgeCaseTest {

    @Test
    fun testNullableMutableCopyTo() {
        // Arrange
        val source = NullableMutableSource(
            nullableProp = "not null value",
            nonNullProp = "non null value",
        )
        val target = NullableMutableTarget(
            nullableProp = null,
            nonNullProp = "old value",
            additionalProp = 42,
        )

        // Act
        val result = source.mutableCopyToNullableMutableTarget(
            mutableTarget = target,
            additionalProp = null
        )

        // Assert
        assertEquals("not null value", result.nullableProp)
        assertEquals("non null value", result.nonNullProp)
        assertNull(result.additionalProp)
        assertEquals(target, result)
    }

    @Test
    fun testNullableMutableCopyToWithNullSource() {
        // Arrange
        val source = NullableMutableSource(
            nullableProp = null,
            nonNullProp = "non null value",
        )
        val target = NullableMutableTarget(
            nullableProp = "old value",
            nonNullProp = "old non null",
            additionalProp = 42,
        )

        // Act
        val result = source.mutableCopyToNullableMutableTarget(
            mutableTarget = target,
            additionalProp = 99
        )

        // Assert
        assertNull(result.nullableProp)
        assertEquals("non null value", result.nonNullProp)
        assertEquals(99, result.additionalProp)
        assertEquals(target, result)
    }

    @Test
    fun testPartialMatchMutableCopyTo() {
        // Arrange
        val source = PartialMatchSource(
            matchingProp1 = "matched value",
            matchingProp2 = 123,
            sourceOnlyProp = true,
        )
        val target = PartialMatchTarget(
            matchingProp1 = "old value",
            matchingProp2 = 0,
            targetOnlyProp = 0.0,
        )

        // Act
        val result = source.mutableCopyToPartialMatchTarget(
            mutableTarget = target,
            targetOnlyProp = 3.14
        )

        // Assert
        assertEquals("matched value", result.matchingProp1)
        assertEquals(123, result.matchingProp2)
        assertEquals(3.14, result.targetOnlyProp)
        assertEquals(target, result)
    }
}
