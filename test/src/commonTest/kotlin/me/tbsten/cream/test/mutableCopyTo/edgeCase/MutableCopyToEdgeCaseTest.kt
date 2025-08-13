package me.tbsten.cream.test.mutableCopyTo.edgeCase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MutableCopyToEdgeCaseTest {

    @Test
    fun testNullableMutableCopyTo() {
        // Arrange
        val source = NullableMutableSource(
            nullableProp = "not_null",
            nonNullProp = "non_null_value",
        )
        val target = NullableMutableTarget(
            nullableProp = null,
            nonNullProp = "old_value",
            additionalProp = 42,
        )

        // Act
        val result = source.copyToNullableMutableTarget(
            mutableTarget = target,
            additionalProp = null
        )

        // Assert
        assertEquals("not_null", result.nullableProp)
        assertEquals("non_null_value", result.nonNullProp)
        assertNull(result.additionalProp)
        assertEquals(target, result)
    }

    @Test
    fun testNullableMutableCopyToWithNullSource() {
        // Arrange
        val source = NullableMutableSource(
            nullableProp = null,
            nonNullProp = "non_null_value",
        )
        val target = NullableMutableTarget(
            nullableProp = "old_value",
            nonNullProp = "old_non_null",
            additionalProp = 42,
        )

        // Act
        val result = source.copyToNullableMutableTarget(
            mutableTarget = target,
            additionalProp = 99
        )

        // Assert
        assertNull(result.nullableProp)
        assertEquals("non_null_value", result.nonNullProp)
        assertEquals(99, result.additionalProp)
        assertEquals(target, result)
    }

    @Test
    fun testPartialMatchMutableCopyTo() {
        // Arrange
        val source = PartialMatchSource(
            matchingProp1 = "matched1",
            matchingProp2 = 123,
            sourceOnlyProp = true,
        )
        val target = PartialMatchTarget(
            matchingProp1 = "old1",
            matchingProp2 = 0,
            targetOnlyProp = 0.0,
        )

        // Act
        val result = source.copyToPartialMatchTarget(
            mutableTarget = target,
            targetOnlyProp = 3.14
        )

        // Assert
        assertEquals("matched1", result.matchingProp1)
        assertEquals(123, result.matchingProp2)
        assertEquals(3.14, result.targetOnlyProp)
        assertEquals(target, result)
    }
}
