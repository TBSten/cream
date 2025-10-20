package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo

@CopyTo.Fun(funName = "createTopLevelFunFactoryDomainModel")
data class TopLevelFunFactoryDataModel(
    @CopyTo.Map("domainId")
    val dataId: String,
    val name: String,
)

data class TopLevelFunFactoryDomainModel(
    val domainId: String,
    val name: String,
    val timestamp: Long,
)

fun createTopLevelFunFactoryDomainModel(
    domainId: String,
    name: String,
    timestamp: Long,
): TopLevelFunFactoryDomainModel = TopLevelFunFactoryDomainModel(domainId = domainId, name = name, timestamp = timestamp)
