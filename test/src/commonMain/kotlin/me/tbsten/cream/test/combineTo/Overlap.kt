package me.tbsten.cream.test.combineTo

import me.tbsten.cream.CombineTo

// Overlapping properties with different types test
@CombineTo(OverlapTarget::class)
data class OverlapSourceA(
    val sharedProperty: String,
    val uniqueA: Int,
)

@CombineTo(OverlapTarget::class)
data class OverlapSourceB(
    val sharedProperty: String,
    val uniqueB: Boolean,
)

data class OverlapTarget(
    val sharedProperty: String,
    val uniqueA: Int,
    val uniqueB: Boolean,
)
