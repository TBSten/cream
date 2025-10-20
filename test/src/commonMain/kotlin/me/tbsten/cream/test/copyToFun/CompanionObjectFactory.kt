package me.tbsten.cream.test.copyToFun

import me.tbsten.cream.CopyTo

@CopyTo.Fun(funName = "CompanionObjectFactoryDomainModel.from")
data class CompanionObjectFactoryDataModel(
    @CopyTo.Map("domainId")
    val dataId: String,
    val name: String,
)

data class CompanionObjectFactoryDomainModel(
    val domainId: String,
    val name: String,
    val timestamp: Long,
) {
    companion object {
        fun from(
            domainId: String,
            name: String,
            timestamp: Long,
        ): CompanionObjectFactoryDomainModel = CompanionObjectFactoryDomainModel(domainId = domainId, name = name, timestamp = timestamp)
    }
}
