package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTargetSimpleName
import me.tbsten.cream.CopyTo

// Token override: the generated name is built from the target's simple name.
@CopyTo(FunNameTarget::class, funName = "to" + CopyTargetSimpleName)
data class FunNameSource(
    val shared: String,
)

data class FunNameTarget(
    val shared: String,
    val extra: Int,
)

// Backtick-quoted literal name (contains a space).
@CopyTo(FunNameBacktickTarget::class, funName = "`to backtick`")
data class FunNameBacktickSource(
    val shared: String,
)

data class FunNameBacktickTarget(
    val shared: String,
)

// Sealed target: a token names each generated child function from its own child.
@CopyTo(FunNameState::class, funName = "into" + CopyTargetSimpleName)
data class FunNameStateSource(
    val id: String,
)

sealed interface FunNameState {
    val id: String

    data class A(
        override val id: String,
    ) : FunNameState

    data class B(
        override val id: String,
        val x: Int,
    ) : FunNameState
}
