package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class PropertyMappingTest :
    FreeSpec({
        "propertyMapping" {
            val domainSource =
                DomainSourceModel(
                    domainId = "test-id",
                    name = "test-name",
                )

            val dataTarget: DataTargetModel = domainSource.copyToDataTargetModel()

            dataTarget shouldBe DataTargetModel(dataId = "test-id", name = "test-name")
        }
    })
