package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo

data class DomainModel(
    val id: String,
)

data class DataModel(
    val id: String,
)

typealias DataModelAlias = DataModel

@CopyTo(DataModelAlias::class) // will generate DomainModelAlias.copyToDataModelAlias(...): DataModelAlias
typealias DomainModelAlias = DomainModel
