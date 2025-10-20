package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class CompanionObjectFactoryTest {
    @Test
    fun companionObjectFactoryWithPropertyMapping() {
        val dataModel =
            CompanionObjectFactoryDataModel(
                dataId = "data-456",
                name = "Test Name",
            )

        val domainModel: CompanionObjectFactoryDomainModel =
            dataModel.copyToCompanionObjectFactoryDomainModel(
                timestamp = 1234567890L,
            )

        assertEquals(
            CompanionObjectFactoryDomainModel(
                domainId = "data-456",
                name = "Test Name",
                timestamp = 1234567890L,
            ),
            domainModel,
        )
    }

    @Test
    fun companionObjectFactoryWithOverride() {
        val dataModel =
            CompanionObjectFactoryDataModel(
                dataId = "data-456",
                name = "Test Name",
            )

        val domainModel: CompanionObjectFactoryDomainModel =
            dataModel.copyToCompanionObjectFactoryDomainModel(
                domainId = "overridden-id",
                name = "Overridden Name",
                timestamp = 9999999999L,
            )

        assertEquals(
            CompanionObjectFactoryDomainModel(
                domainId = "overridden-id",
                name = "Overridden Name",
                timestamp = 9999999999L,
            ),
            domainModel,
        )
    }
}
