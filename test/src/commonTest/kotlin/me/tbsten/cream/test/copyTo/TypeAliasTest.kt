package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TypeAliasTest {
    @Test
    fun testCopyToTypeAlias() {
        // Create a DomainModel instance (through DomainModelAlias)
        val domainModel: DomainModelAlias = DomainModel(id = "test-id-123")

        // Call the generated copyToDataModel function
        // Note: Function name is based on the actual type (DataModel), not the alias
        val dataModel: DataModelAlias = domainModel.copyToDataModel()

        // Verify the id property is copied correctly
        assertEquals("test-id-123", dataModel.id)

        // Verify the result is actually a DataModel instance
        assertIs<DataModel>(dataModel)
    }

    @Test
    fun testTypeAliasPreservesIdentity() {
        val original: DomainModelAlias = DomainModel(id = "original-id")
        val copied: DataModelAlias = original.copyToDataModel()

        // Verify that the copied object has the same property values
        assertEquals(original.id, copied.id)
    }
}
