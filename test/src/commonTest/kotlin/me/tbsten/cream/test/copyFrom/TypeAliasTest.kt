package me.tbsten.cream.test.copyFrom

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TypeAliasTest {
    @Test
    fun testCopyFromTypeAlias() {
        // Create a SourceModel instance (through SourceModelAlias)
        val sourceModel: SourceModelAlias = SourceModel(value = "test-value")

        // Call the generated copyToTargetModel function
        // Note: Function name is based on the actual type (TargetModel), not the alias
        val targetModel: TargetModelAlias = sourceModel.copyToTargetModel()

        // Verify the value property is copied correctly
        assertEquals("test-value", targetModel.value)

        // Verify the result is actually a TargetModel instance
        assertIs<TargetModel>(targetModel)
    }

    @Test
    fun testTypeAliasPreservesValue() {
        val original: SourceModelAlias = SourceModel(value = "original-value")
        val copied: TargetModelAlias = original.copyToTargetModel()

        // Verify that the copied object has the same property values
        assertEquals(original.value, copied.value)
    }
}
