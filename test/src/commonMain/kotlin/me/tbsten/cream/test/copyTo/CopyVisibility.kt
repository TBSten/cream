package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyVisibility

// Generated copy function is forced to `internal` via the visibility argument.
@CopyTo(InternalVisibilityTarget::class, visibility = CopyVisibility.INTERNAL)
data class InternalVisibilitySource(
    val shared: String,
)

data class InternalVisibilityTarget(
    val shared: String,
    val extra: Int,
)
