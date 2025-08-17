package me.tbsten.cream.test.mutableCopyFrom

import kotlin.test.Test
import kotlin.test.assertEquals

class MutableCopyFromTest {

    @Test
    fun testMutableCopyFrom() {
        // Arrange
        val source = DataLayerModel(
            data = "test string",
            count = 42,
            enabled = true,
        )
        val target = DomainLayerModel(
            data = "old value",
            count = 0,
            enabled = false,
            extraProp = "old extra",
        )

        // Act
        val result = source.mutableCopyToDomainLayerModel(
            mutableTarget = target,
            extraProp = "new extra"
        )

        // Assert
        assertEquals("test string", result.data)
        assertEquals(42, result.count)
        assertEquals(true, result.enabled)
        assertEquals("new extra", result.extraProp)
        assertEquals(target, result)
    }

    @Test
    fun testMutableCopyFromWithCustomPrefix() {
        // Arrange
        val source = DataLayerModel(
            data = "test string",
            count = 42,
            enabled = true,
        )
        val target = CustomPrefixModel(
            data = "old value",
            count = 0,
            enabled = false,
        )

        // Act
        val result = source.mutableCopyToCustomPrefixModel(
            mutableTarget = target
        )

        // Assert
        assertEquals("test string", result.data)
        assertEquals(42, result.count)
        assertEquals(true, result.enabled)
        assertEquals(target, result)
    }
}
