package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class TopLevelFunFactoryTest {
    @Test
    fun topLevelFunctionFactoryWithPropertyMapping() {
        val dataModel =
            TopLevelFunFactoryDataModel(
                dataId = "data-123",
                name = "Test Name",
            )

        val domainModel: TopLevelFunFactoryDomainModel =
            dataModel.copyToTopLevelFunFactoryDomainModel(
                timestamp = 1234567890L,
            )

        assertEquals(
            TopLevelFunFactoryDomainModel(
                domainId = "data-123",
                name = "Test Name",
                timestamp = 1234567890L,
            ),
            domainModel,
        )
    }

    @Test
    fun topLevelFunctionFactoryWithOverride() {
        val dataModel =
            TopLevelFunFactoryDataModel(
                dataId = "data-123",
                name = "Test Name",
            )

        val domainModel: TopLevelFunFactoryDomainModel =
            dataModel.copyToTopLevelFunFactoryDomainModel(
                domainId = "overridden-id",
                name = "Overridden Name",
                timestamp = 9999999999L,
            )

        assertEquals(
            TopLevelFunFactoryDomainModel(
                domainId = "overridden-id",
                name = "Overridden Name",
                timestamp = 9999999999L,
            ),
            domainModel,
        )
    }
}
