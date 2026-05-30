package me.tbsten.cream.test.sealedCopy

import me.tbsten.cream.SealedCopy

@SealedCopy
sealed interface BasicState {
    val sessionId: String
    val attempt: Int

    data class Loading(
        override val sessionId: String,
        override val attempt: Int,
    ) : BasicState

    data class Success(
        override val sessionId: String,
        override val attempt: Int,
        val payload: String,
    ) : BasicState
}
