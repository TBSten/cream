package me.tbsten.cream.test.combineFrom.edgeCase

import me.tbsten.cream.CombineFrom

/**
 * Test overlapping properties from multiple sources
 * When multiple sources have the same property name,
 * the last source class (in declaration order) should take precedence
 */

data class OverlapSourceA(
    val shared: String,
    val uniqueA: String,
)

data class OverlapSourceB(
    val shared: String,
    val uniqueB: Int,
)

@CombineFrom(OverlapSourceA::class, OverlapSourceB::class)
data class OverlapTarget(
    val shared: String,
    val uniqueA: String,
    val uniqueB: Int,
)
