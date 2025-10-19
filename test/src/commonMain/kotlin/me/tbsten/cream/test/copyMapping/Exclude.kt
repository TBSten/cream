package me.tbsten.cream.test.copyMapping

import me.tbsten.cream.CopyMapping

data class CopyMappingExcludeTestSource(
    val sharedProp: String,
    val excludedProp: String,
)

data class CopyMappingExcludeTestTarget(
    val sharedProp: String,
    val excludedProp: String,
    val targetProp: String,
)

@CopyMapping(
    source = CopyMappingExcludeTestSource::class,
    target = CopyMappingExcludeTestTarget::class,
    excludes = ["excludedProp"],
)
private object CopyMappingExcludeTestMapping
