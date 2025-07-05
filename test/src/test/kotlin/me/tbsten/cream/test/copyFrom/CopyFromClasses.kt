package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

@CopyFrom(DomainLayerModel::class)
data class DataLayerModel(
    val prop1: String,
    val prop2: Int,
)

@CopyFrom(DataLayerModel::class)
data class DomainLayerModel(
    val prop1: String,
    val prop2: Int,
)
