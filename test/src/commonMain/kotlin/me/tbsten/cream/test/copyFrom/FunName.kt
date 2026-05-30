package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTargetSimpleName

data class FunNameSrc(
    val shared: String,
)

@CopyFrom(FunNameSrc::class, funName = "build" + CopyTargetSimpleName)
data class FunNameDst(
    val shared: String,
    val extra: Int,
)
