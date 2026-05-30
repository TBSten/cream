package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class TypeAliasTest :
    FunSpec({
        test("testCopyFromTypeAlias") {
            // Create a SourceModel instance and assign it to a variable typed as SourceModelAlias.
            // Note: SourceModelAlias is just a type alias for SourceModel.
            val sourceModel: SourceModelAlias = SourceModel(value = "test-value")

            // Call the generated copyToTargetModel function
            // Note: Function name is based on the actual type (TargetModel), not the alias
            val targetModel: TargetModelAlias = sourceModel.copyToTargetModel()

            // Verify the value property is copied correctly
            targetModel.value shouldBe "test-value"

            // Verify the result is actually a TargetModel instance
            targetModel.shouldBeInstanceOf<TargetModel>()
        }

        test("testTypeAliasPreservesValue") {
            val original: SourceModelAlias = SourceModel(value = "original-value")
            val copied: TargetModelAlias = original.copyToTargetModel()

            // Verify that the copied object has the same property values
            copied.value shouldBe original.value
        }
    })
