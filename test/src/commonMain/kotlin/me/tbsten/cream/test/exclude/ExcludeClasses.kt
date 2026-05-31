package me.tbsten.cream.test.exclude

import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.SealedCopy

// --- @CopyFrom.Exclude ---

sealed interface CopyFromState {
    val name: String
    val count: Int

    data class Loading(
        override val name: String,
        override val count: Int,
    ) : CopyFromState

    @CopyFrom(CopyFromState::class)
    data class Success(
        val name: String,
        @CopyFrom.Exclude val count: Int,
    )
}

// --- @CopyTo.Exclude ---

@CopyTo(CopyToTarget::class)
data class CopyToSource(
    val name: String,
    @CopyTo.Exclude val count: Int,
)

data class CopyToTarget(
    val name: String,
    val count: Int,
)

// --- @CombineFrom.Exclude ---

data class CombineFromLoading(
    val itemId: String,
)

data class CombineFromAction(
    val data: String,
)

@CombineFrom(CombineFromLoading::class, CombineFromAction::class)
data class CombineFromSuccess(
    val itemId: String,
    @CombineFrom.Exclude val data: String,
    val extra: Int,
)

// --- @CombineTo.Exclude ---

@CombineTo(CombineToTarget::class)
data class CombineToSource(
    val itemId: String,
    @CombineTo.Exclude val sessionId: String,
)

data class CombineToTarget(
    val itemId: String,
    val sessionId: String,
    val extra: Int,
)

// --- @SealedCopy.Exclude ---

@SealedCopy
sealed interface SealedCopyExcludeState {
    val name: String

    @SealedCopy.Exclude val count: Int

    data class Loading(
        override val name: String,
        override val count: Int,
    ) : SealedCopyExcludeState

    data class Success(
        override val name: String,
        override val count: Int,
        val data: String,
    ) : SealedCopyExcludeState
}

// --- @CopyToChildren.Exclude ---

@CopyToChildren
sealed interface CopyToChildrenExcludeState {
    val sessionId: String

    @CopyToChildren.Exclude val count: Int

    data class Loading(
        override val sessionId: String,
        override val count: Int,
    ) : CopyToChildrenExcludeState

    data class Success(
        override val sessionId: String,
        override val count: Int,
        val data: String,
    ) : CopyToChildrenExcludeState
}
