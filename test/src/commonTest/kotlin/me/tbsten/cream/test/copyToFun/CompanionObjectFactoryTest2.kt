package me.tbsten.cream.test.copyToFun

import kotlin.test.Test
import kotlin.test.assertEquals

class CompanionObjectFactoryTest2 {
    @Test
    fun companionObjectFactoryWithPropertyMapping() {
        val dataModel =
            CompanionObjectFactoryDataModel(
                dataId = "data-456",
                name = "Test Name 2",
            )

        val domainModel: CompanionObjectFactoryDomainModel =
            dataModel.copyToCompanionObjectFactoryDomainModel(
                timestamp = 2345678901L,
            )

        assertEquals(
            CompanionObjectFactoryDomainModel(
                domainId = "data-456",
                name = "Test Name 2",
                timestamp = 2345678901L,
            ),
            domainModel,
        )
    }

    @Test
    fun companionObjectFactoryWithOverride() {
        val dataModel =
            CompanionObjectFactoryDataModel(
                dataId = "data-456",
                name = "Test Name 2",
            )

        val domainModel: CompanionObjectFactoryDomainModel =
            dataModel.copyToCompanionObjectFactoryDomainModel(
                domainId = "overridden-id-2",
                name = "Overridden Name 2",
                timestamp = 8888888888L,
            )

        assertEquals(
            CompanionObjectFactoryDomainModel(
                domainId = "overridden-id-2",
                name = "Overridden Name 2",
                timestamp = 8888888888L,
            ),
            domainModel,
        )
    }
}
