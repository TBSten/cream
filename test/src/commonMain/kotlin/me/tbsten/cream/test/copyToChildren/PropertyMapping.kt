package me.tbsten.cream.test.copyToChildren

import me.tbsten.cream.CopyToChildren

// Property mapping with @CopyToChildren.Map
@CopyToChildren
sealed interface MappedState {
    @CopyToChildren.Map("loadingId", "successId")
    val stateId: String
    val name: String
}

data class MappedLoading(
    val loadingId: String,
    override val name: String,
) : MappedState {
    override val stateId: String get() = loadingId
}

data class MappedSuccess(
    val successId: String,
    override val name: String,
    val data: String,
) : MappedState {
    override val stateId: String get() = successId
}
