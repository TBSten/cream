package me.tbsten.cream.test.copyMapping

import me.tbsten.cream.CopyMapping
import me.tbsten.cream.CopyTargetSimpleName

data class FunNameMapA(
    val shared: String,
)

data class FunNameMapB(
    val shared: String,
    val extra: Int,
)

@CopyMapping(FunNameMapA::class, FunNameMapB::class, funName = "to" + CopyTargetSimpleName)
object FunNameMapping

data class FunNameRevA(
    val shared: String,
)

data class FunNameRevB(
    val shared: String,
)

// canReverse + token: forward and reverse names derive from their own targets.
@CopyMapping(FunNameRevA::class, FunNameRevB::class, canReverse = true, funName = "to" + CopyTargetSimpleName)
object FunNameRevMapping
