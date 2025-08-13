package me.tbsten.cream.test.mutableCopyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class MutableCopyToTest {

    @Test
    fun testBasicMutableCopyTo() {
        // Arrange
        val source = MutableSource(
            sourceProp1 = "test1",
            sourceProp2 = 42,
            sharedProp = "shared",
        )
        val target = MutableTarget(
            sourceProp1 = "old1",
            sourceProp2 = 0,
            sharedProp = "old_shared",
            targetOnlyProp = "target_only",
        )

        // Act
        val result = source.copyToMutableTarget(
            mutableTarget = target,
            targetOnlyProp = "new_target_only"
        )

        // Assert
        assertEquals("test1", result.sourceProp1)
        assertEquals(42, result.sourceProp2)
        assertEquals("shared", result.sharedProp)
        assertEquals("new_target_only", result.targetOnlyProp)
        
        // Verify that the returned object is the same instance as the target
        assertEquals(target, result)
    }

    @Test
    fun testMutableCopyToWithCustomization() {
        // Arrange
        val source = MutableSource(
            sourceProp1 = "test1",
            sourceProp2 = 42,
            sharedProp = "shared",
        )
        val target = MutableTarget(
            sourceProp1 = "old1",
            sourceProp2 = 0,
            sharedProp = "old_shared",
            targetOnlyProp = "target_only",
        )

        // Act
        val result = source.copyToMutableTarget(
            mutableTarget = target,
            sourceProp1 = "customized1",
            targetOnlyProp = "customized_target"
        )

        // Assert
        assertEquals("customized1", result.sourceProp1)
        assertEquals(42, result.sourceProp2) // From source (default)
        assertEquals("shared", result.sharedProp) // From source (default)
        assertEquals("customized_target", result.targetOnlyProp) // Customized
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
        val result = source.copyToComplexMutableTarget(
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
