package me.tbsten.cream.test.copyFrom

import kotlin.test.Test
import kotlin.test.assertEquals

class PropertyMappingTest {
    @Test
    fun propertyMapping() {
        val dataModel =
            DataModel(
                dataId = "test-id",
                name = "test-name",
            )

        val domainModel: DomainModel = dataModel.copyToDomainModel()

        assertEquals(
            DomainModel(domainId = "test-id", name = "test-name"),
            domainModel,
        )
    }
}
