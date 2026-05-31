package me.tbsten.cream.test.combineTo

import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyTargetSimpleName

@CombineTo(FunNameDst::class, funName = "to" + CopyTargetSimpleName)
data class FunNameSrc(
    val a: String,
)

data class FunNameDst(
    val a: String,
    val extra: Int,
)
