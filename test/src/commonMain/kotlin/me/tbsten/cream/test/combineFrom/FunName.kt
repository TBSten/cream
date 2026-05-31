package me.tbsten.cream.test.combineFrom

import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CopyTargetSimpleName

data class FunNameA(
    val a: String,
)

data class FunNameB(
    val b: Int,
)

@CombineFrom(FunNameA::class, FunNameB::class, funName = "build" + CopyTargetSimpleName)
data class FunNameDst(
    val a: String,
    val b: Int,
    val extra: Long,
)
