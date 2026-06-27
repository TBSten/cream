package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class BasicTest :
    FreeSpec({
        "dataLayerModelToDomainLayerModel" {
            val dataLayerModel: DataLayerModel =
                DataLayerModel(
                    prop1 = "prop1",
                    prop2 = 42,
                )

            mapOf(
                dataLayerModel.copyToDomainLayerModel() to
                    DomainLayerModel(
                        prop1 = "prop1",
                        prop2 = 42,
                    ),
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }

        "domainLayerModelToDataLayerModel" {
            val domainLayerModel: DomainLayerModel =
                DomainLayerModel(
                    prop1 = "prop1",
                    prop2 = 42,
                )

            mapOf(
                domainLayerModel.copyToDataLayerModel() to
                    DataLayerModel(
                        prop1 = "prop1",
                        prop2 = 42,
                    ),
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }
    })
