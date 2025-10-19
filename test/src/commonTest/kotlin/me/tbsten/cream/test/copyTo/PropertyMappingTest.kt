package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class PropertyMappingTest {
    @Test
    fun propertyMapping() {
        val domainSource =
            DomainSourceModel(
                domainId = "test-id",
                name = "test-name",
            )

        val dataTarget = domainSource.copyToDataTargetModel()

        assertEquals(
            DataTargetModel(dataId = "test-id", name = "test-name"),
            dataTarget,
        )
    }
}
