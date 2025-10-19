package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo

// Property mapping with @CopyTo.Map
@CopyTo(DataTargetModel::class, UiTargetModel::class)
data class DomainSourceModel(
    @CopyTo.Map("dataId", "uiId")
    val domainId: String,
    val name: String,
)

data class DataTargetModel(
    val dataId: String,
    val name: String,
)

data class UiTargetModel(
    val uiId: String,
    val name: String,
)
