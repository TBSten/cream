package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo

@CopyTo(CopyToExcludeTestTarget::class)
data class CopyToExcludeTestSource(
    val sharedProp: String,
    @CopyTo.Exclude
    val excludedProp: String,
)

data class CopyToExcludeTestTarget(
    val sharedProp: String,
    val excludedProp: String,
    val targetProp: String,
)
