package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PropertyMappingTest :
    FunSpec({
        test("propertyMapping") {
            val dataModel =
                DataModel(
                    dataId = "test-id",
                    name = "test-name",
                )

            val domainModel: DomainModel = dataModel.copyToDomainModel()

            domainModel shouldBe DomainModel(domainId = "test-id", name = "test-name")
        }
    })
