package me.tbsten.cream.test.combineFrom

import me.tbsten.cream.CombineFrom

data class ExcludeTestSourceA(
    val sourceAProperty: String,
)

data class ExcludeTestSourceB(
    val sourceBProperty: String,
)

@CombineFrom(ExcludeTestSourceA::class, ExcludeTestSourceB::class)
data class ExcludeTestTarget(
    val sourceAProperty: String,
    @CombineFrom.Exclude
    val sourceBProperty: String,
)
