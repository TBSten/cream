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
        source.copyToNullableMutableTarget(target) {
            additionalProp = null
        }

        // Assert
        assertEquals("not_null", target.nullableProp)
        assertEquals("non_null_value", target.nonNullProp)
        assertNull(target.additionalProp)
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
        source.copyToNullableMutableTarget(target)

        // Assert
        assertNull(target.nullableProp)
        assertEquals("non_null_value", target.nonNullProp)
        assertEquals(42, target.additionalProp) // Unchanged
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
        source.copyToPartialMatchTarget(target) {
            targetOnlyProp = 3.14
        }

        // Assert
        assertEquals("matched1", target.matchingProp1)
        assertEquals(123, target.matchingProp2)
        assertEquals(3.14, target.targetOnlyProp)
    }
}
