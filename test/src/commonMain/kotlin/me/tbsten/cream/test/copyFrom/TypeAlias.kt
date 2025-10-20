package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

data class SourceModel(
    val value: String,
)

data class TargetModel(
    val value: String,
)

typealias SourceModelAlias = SourceModel

@CopyFrom(SourceModelAlias::class) // will generate SourceModel.copyToTargetModel(...): TargetModel
typealias TargetModelAlias = TargetModel
