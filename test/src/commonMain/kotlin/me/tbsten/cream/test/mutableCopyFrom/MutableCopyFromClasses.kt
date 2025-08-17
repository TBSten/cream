package me.tbsten.cream.test.mutableCopyFrom

import me.tbsten.cream.MutableCopyFrom

data class DataLayerModel(
    val data: String,
    val count: Int,
    val enabled: Boolean,
)

@MutableCopyFrom(DataLayerModel::class)
data class DomainLayerModel(
    var data: String,
    var count: Int,
    var enabled: Boolean,
    var extraProp: String,
)

@MutableCopyFrom(DataLayerModel::class)
data class CustomPrefixModel(
    var data: String,
    var count: Int,
    var enabled: Boolean,
)
