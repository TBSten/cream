package me.tbsten.cream.test.mutableCopyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class MutableCopyToWithOptionsTest {

    @Test
    fun testMutableCopyToWithCustomPrefix() {
        // Arrange
        val source = MutableSourceWithOptions(
            prop1 = "test string",
            prop2 = 42,
        )
        val target = MutableTargetWithOptions(
            prop1 = "old value",
            prop2 = 0,
            extraProp = "old extra",
        )

        // Act
        val result = source.updateWithMutableTargetWithOptions(
            mutableTarget = target,
            extraProp = "new extra"
        )

        // Assert
        assertEquals("test string", result.prop1)
        assertEquals(42, result.prop2)
        assertEquals("new extra", result.extraProp)
        assertEquals(target, result)
    }
}
