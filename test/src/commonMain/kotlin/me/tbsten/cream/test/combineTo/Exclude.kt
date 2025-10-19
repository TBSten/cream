package me.tbsten.cream.test.combineTo

import me.tbsten.cream.CombineTo

@CombineTo(CombineToExcludeTestTarget::class)
data class CombineToExcludeTestSourceA(
    val sourceAProp: String,
    @CombineTo.Exclude
    val excludedProp: String,
)

@CombineTo(CombineToExcludeTestTarget::class)
data class CombineToExcludeTestSourceB(
    val sourceBProp: String,
)

data class CombineToExcludeTestTarget(
    val sourceAProp: String,
    val excludedProp: String,
    val sourceBProp: String,
)
