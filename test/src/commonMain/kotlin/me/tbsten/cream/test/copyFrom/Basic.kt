package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

/**
 * Data layer model.
 *
 * @property prop1
 * @property prop2
 */
@CopyFrom(DomainLayerModel::class)
data class DataLayerModel(
    val prop1: String,
    val prop2: Int,
)

/**
 * Domain layer model.
 *
 * @property prop1
 * @property prop2
 */
@CopyFrom(DataLayerModel::class)
data class DomainLayerModel(
    val prop1: String,
    val prop2: Int,
)
