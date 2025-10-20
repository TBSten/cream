package me.tbsten.cream.test.combineFrom

import me.tbsten.cream.CombineFrom

// Source classes for combining
data class SourceX(
    val propX: String,
)

data class SourceY(
    val propY: Int,
)

// Target class that combines properties from both sources
data class CombinedResult(
    val propX: String,
    val propY: Int,
    val extraProp: String,
)

// Typealiases for sources and target
typealias SourceXAlias = SourceX
typealias SourceYAlias = SourceY
typealias CombinedResultAlias = CombinedResult

// Test case: typealias as annotation target with typealias sources
@CombineFrom(SourceXAlias::class, SourceYAlias::class)
typealias CombinedResultWithAlias = CombinedResult
