package me.tbsten.cream.test.combineMapping

import me.tbsten.cream.CombineMapping
import me.tbsten.cream.CopyTargetSimpleName

data class FunNameCmA(
    val a: String,
)

data class FunNameCmB(
    val b: Int,
)

data class FunNameCmDst(
    val a: String,
    val b: Int,
    val extra: Long,
)

@CombineMapping(
    sources = [FunNameCmA::class, FunNameCmB::class],
    target = FunNameCmDst::class,
    funName = "to" + CopyTargetSimpleName,
)
object FunNameCmMapping
