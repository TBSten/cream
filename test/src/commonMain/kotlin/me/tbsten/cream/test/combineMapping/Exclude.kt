package me.tbsten.cream.test.combineMapping

import me.tbsten.cream.CombineMapping

data class CombineMappingExcludeTestSourceA(
    val sourceAProp: String,
    val excludedProp: String,
)

data class CombineMappingExcludeTestSourceB(
    val sourceBProp: String,
)

data class CombineMappingExcludeTestTarget(
    val sourceAProp: String,
    val excludedProp: String,
    val sourceBProp: String,
)

@CombineMapping(
    sources = [
        CombineMappingExcludeTestSourceA::class,
        CombineMappingExcludeTestSourceB::class,
    ],
    target = CombineMappingExcludeTestTarget::class,
    properties = [
        CombineMapping.Exclude(source = "excludedProp"),
    ],
)
private object CombineMappingExcludeTestMapping
