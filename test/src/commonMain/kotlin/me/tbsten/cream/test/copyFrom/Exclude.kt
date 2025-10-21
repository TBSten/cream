package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

data class CopyFromExcludeTestSource(
    val sharedProp: String,
    val sourceProp: String,
)

@CopyFrom(CopyFromExcludeTestSource::class)
data class CopyFromExcludeTestTarget(
    val sharedProp: String,
    @CopyFrom.Exclude("CopyFromExcludeTestSource")
    val sourceProp: String,
    val targetProp: String,
)
