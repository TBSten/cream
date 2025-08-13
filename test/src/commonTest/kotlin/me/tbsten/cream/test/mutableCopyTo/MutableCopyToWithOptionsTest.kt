package me.tbsten.cream.test.mutableCopyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class MutableCopyToWithOptionsTest {

    @Test
    fun testMutableCopyToWithCustomPrefix() {
        // Arrange
        val source = MutableSourceWithOptions(
            prop1 = "test1",
            prop2 = 42,
        )
        val target = MutableTargetWithOptions(
            prop1 = "old1",
            prop2 = 0,
            extraProp = "old_extra",
        )

        // Act
        val result = source.updateWithMutableTargetWithOptions(
            mutableTarget = target,
            extraProp = "new_extra"
        )

        // Assert
        assertEquals("test1", result.prop1)
        assertEquals(42, result.prop2)
        assertEquals("new_extra", result.extraProp)
        assertEquals(target, result)
    }
}
