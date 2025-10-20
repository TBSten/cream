package me.tbsten.cream.test.combineTo

import me.tbsten.cream.CombineTo

// Source classes for combining
data class SourceA(
    val propA: String,
)

data class SourceB(
    val propB: Int,
)

// Target class that combines properties from both sources
data class CombinedTarget(
    val propA: String,
    val propB: Int,
    val extra: String,
)

// Typealiases for sources and target
typealias SourceAAlias = SourceA
typealias SourceBAlias = SourceB
typealias CombinedTargetAlias = CombinedTarget

// Test case 1: typealias as annotation target (source)
@CombineTo(CombinedTargetAlias::class)
typealias SourceAWithAlias = SourceA

@CombineTo(CombinedTargetAlias::class)
typealias SourceBWithAlias = SourceB
