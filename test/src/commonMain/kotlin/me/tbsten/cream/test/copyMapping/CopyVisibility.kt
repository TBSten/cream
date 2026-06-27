package me.tbsten.cream.test.copyMapping

import me.tbsten.cream.CopyMapping
import me.tbsten.cream.CopyVisibility

/**
 * Library models whose generated copy functions are forced to `internal` via the `visibility`
 * argument. With `canReverse = true`, the same visibility must be applied to BOTH the forward
 * (`LibA -> LibB`) and reverse (`LibB -> LibA`) functions.
 */
data class InternalVisibilityLibA(
    val shared: String,
    val aProp: Int,
)

data class InternalVisibilityLibB(
    val shared: String,
    val bProp: Boolean,
)

@CopyMapping(
    InternalVisibilityLibA::class,
    InternalVisibilityLibB::class,
    canReverse = true,
    visibility = CopyVisibility.INTERNAL,
)
private object InternalVisibilityMapping
