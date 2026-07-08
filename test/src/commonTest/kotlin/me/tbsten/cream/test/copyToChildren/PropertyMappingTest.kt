package me.tbsten.cream.test.copyToChildren

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class PropertyMappingTest :
    FreeSpec({
        "mappedPropertySuppliesRenamedParameter" {
            val state: MappedState = MappedLoading(loadingId = "id-1", name = "name-1")

            val success: MappedSuccess = state.copyToMappedSuccess(data = "data-1")

            success shouldBe MappedSuccess(successId = "id-1", name = "name-1", data = "data-1")
        }

        "mappedPropertyFansOutToEachChildParameterName" {
            val state: MappedState = MappedSuccess(successId = "id-2", name = "name-2", data = "data-2")

            val loading: MappedLoading = state.copyToMappedLoading()

            loading shouldBe MappedLoading(loadingId = "id-2", name = "name-2")
        }

        "explicitArgumentOverridesMappedDefault" {
            val state: MappedState = MappedLoading(loadingId = "id-3", name = "name-3")

            val success: MappedSuccess = state.copyToMappedSuccess(successId = "override-id", data = "data-3")

            success shouldBe MappedSuccess(successId = "override-id", name = "name-3", data = "data-3")
        }
    })
