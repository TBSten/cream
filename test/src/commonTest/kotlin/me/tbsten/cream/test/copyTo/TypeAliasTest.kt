package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class TypeAliasTest :
    FreeSpec({
        "testCopyToTypeAlias" {
            // Create a DomainModel instance and assign it to a variable typed as DomainModelAlias.
            // Note: DomainModelAlias is just a type alias for DomainModel.
            val domainModel: DomainModelAlias = DomainModel(id = "test-id-123")

            // Call the generated copyToDataModel function
            // Note: Function name is based on the actual type (DataModel), not the alias
            val dataModel: DataModelAlias = domainModel.copyToDataModel()

            // Verify the id property is copied correctly
            dataModel.id shouldBe "test-id-123"

            // Verify the result is actually a DataModel instance
            dataModel.shouldBeInstanceOf<DataModel>()
        }

        "testTypeAliasPreservesIdentity" {
            val original: DomainModelAlias = DomainModel(id = "original-id")
            val copied: DataModelAlias = original.copyToDataModel()

            // Verify that the copied object has the same property values
            copied.id shouldBe original.id
        }
    })
