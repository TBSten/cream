package me.tbsten.cream.test.combineFrom

import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CopyVisibility

data class InternalVisibilityCombineSource(
    val shared: String,
)

// Generated combine function is forced to `internal` via the visibility argument.
@CombineFrom(InternalVisibilityCombineSource::class, visibility = CopyVisibility.INTERNAL)
data class InternalVisibilityCombineTarget(
    val shared: String,
    val extra: Int,
)
