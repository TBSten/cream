package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

// Property mapping with @CopyFrom.Map
@CopyFrom(DataModel::class, UiModel::class)
data class DomainModel(
    @CopyFrom.Map("dataId", "uiId")
    val domainId: String,
    val name: String,
)

data class DataModel(
    val dataId: String,
    val name: String,
)

data class UiModel(
    val uiId: String,
    val name: String,
)
