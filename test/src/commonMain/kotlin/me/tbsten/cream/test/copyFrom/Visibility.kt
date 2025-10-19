package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

// Different visibility properties
@CopyFrom(VisibilitySource::class)
data class VisibilityTarget(
    val publicProp: String,
    val newProperty: String,
)

data class VisibilitySource(
    val publicProp: String,
    internal val internalProp: Int,
    private val privateProp: Boolean,
)
