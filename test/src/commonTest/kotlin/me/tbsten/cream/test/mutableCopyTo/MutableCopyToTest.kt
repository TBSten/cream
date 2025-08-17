package me.tbsten.cream.test.mutableCopyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class MutableCopyToTest {

    @Test
    fun testBasicMutableCopyTo() {
        // Arrange
        val source = MutableSource(
            sourceProp1 = "test string",
            sourceProp2 = 42,
            sharedProp = "shared value",
        )
        val target = MutableTarget(
            sourceProp1 = "old value",
            sourceProp2 = 0,
            sharedProp = "old shared",
            targetOnlyProp = "target only",
        )

        // Act
        val result = source.mutableCopyToMutableTarget(
            mutableTarget = target,
            targetOnlyProp = "new target only"
        )

        // Assert
        assertEquals("test string", result.sourceProp1)
        assertEquals(42, result.sourceProp2)
        assertEquals("shared value", result.sharedProp)
        assertEquals("new target only", result.targetOnlyProp)

        // Verify that the returned object is the same instance as the target
        assertEquals(target, result)
    }

    @Test
    fun testMutableCopyToWithCustomization() {
        // Arrange
        val source = MutableSource(
            sourceProp1 = "test string",
            sourceProp2 = 42,
            sharedProp = "shared value",
        )
        val target = MutableTarget(
            sourceProp1 = "old value",
            sourceProp2 = 0,
            sharedProp = "old shared",
            targetOnlyProp = "target only",
        )

        // Act
        val result = source.mutableCopyToMutableTarget(
            mutableTarget = target,
            sourceProp1 = "customized value",
            targetOnlyProp = "customized target"
        )

        // Assert
        assertEquals("customized value", result.sourceProp1)
        assertEquals(42, result.sourceProp2) // From source (default)
        assertEquals("shared value", result.sharedProp) // From source (default)
        assertEquals("customized target", result.targetOnlyProp) // Customized
        assertEquals(target, result)
    }

    @Test
    fun testComplexMutableCopyTo() {
        // Arrange
        val source = ComplexMutableSource(
            name = "Test Name",
            count = 100,
            enabled = true,
        )
        val target = ComplexMutableTarget(
            name = "Old Name",
            count = 0,
            enabled = false,
            description = "Old Description",
            metadata = emptyMap(),
        )

        // Act
        val result = source.mutableCopyToComplexMutableTarget(
            mutableTarget = target,
            description = "Updated Description",
            metadata = mapOf("key" to "value")
        )

        // Assert
        assertEquals("Test Name", result.name)
        assertEquals(100, result.count)
        assertEquals(true, result.enabled)
        assertEquals("Updated Description", result.description)
        assertEquals(mapOf("key" to "value"), result.metadata)
        assertEquals(target, result)
    }
}
