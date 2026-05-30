package me.tbsten.cream.test.combineTo

import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyVisibility

// Generated combine function is forced to `internal` via the visibility argument.
@CombineTo(InternalVisibilityCombineTarget::class, visibility = CopyVisibility.INTERNAL)
data class InternalVisibilityCombineSource(
    val shared: String,
)

data class InternalVisibilityCombineTarget(
    val shared: String,
    val extra: Int,
)
