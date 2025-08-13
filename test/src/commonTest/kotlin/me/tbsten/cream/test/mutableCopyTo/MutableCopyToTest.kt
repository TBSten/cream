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
        source.copyToMutableTarget(target)

        // Assert
        assertEquals("test1", target.sourceProp1)
        assertEquals(42, target.sourceProp2)
        assertEquals("shared", target.sharedProp)
        assertEquals("target_only", target.targetOnlyProp) // Should remain unchanged
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
        source.copyToMutableTarget(target) {
            sourceProp1 = "customized1"
            targetOnlyProp = "customized_target"
        }

        // Assert
        assertEquals("customized1", target.sourceProp1)
        assertEquals(42, target.sourceProp2) // From source
        assertEquals("shared", target.sharedProp) // From source
        assertEquals("customized_target", target.targetOnlyProp) // Customized
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
        source.copyToComplexMutableTarget(target) {
            description = "Updated Description"
            metadata = mapOf("key" to "value")
        }

        // Assert
        assertEquals("Test Name", target.name)
        assertEquals(100, target.count)
        assertEquals(true, target.enabled)
        assertEquals("Updated Description", target.description)
        assertEquals(mapOf("key" to "value"), target.metadata)
    }
}
