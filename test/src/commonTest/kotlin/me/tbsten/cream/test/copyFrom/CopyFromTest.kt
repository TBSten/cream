package me.tbsten.cream.test.copyFrom

import kotlin.test.Test
import kotlin.test.assertEquals

class CopyFromTest {
    @Test
    fun dataLayerModelToDomainLayerModel() {
        val dataLayerModel: DataLayerModel = DataLayerModel(
            prop1 = "prop1",
            prop2 = 42,
        )

        mapOf(
            dataLayerModel.copyToDomainLayerModel() to DomainLayerModel(
                prop1 = "prop1",
                prop2 = 42,
            ),
        ).forEach { (actual, expected) ->
            assertEquals(actual, expected)
        }
    }

    @Test
    fun domainLayerModelToDataLayerModel() {
        val domainLayerModel: DomainLayerModel = DomainLayerModel(
            prop1 = "prop1",
            prop2 = 42,
        )

        mapOf(
            domainLayerModel.copyToDataLayerModel() to DataLayerModel(
                prop1 = "prop1",
                prop2 = 42,
            ),
        ).forEach { (actual, expected) ->
            assertEquals(actual, expected)
        }
    }
}
