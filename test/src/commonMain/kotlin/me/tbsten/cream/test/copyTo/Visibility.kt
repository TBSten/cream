package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo

// Different visibility properties
@CopyTo(VisibilityTarget::class)
data class VisibilitySource(
    val publicProp: String,
    internal val internalProp: Int,
    private val privateProp: Boolean,
)

data class VisibilityTarget(
    val publicProp: String,
    val newProperty: String,
)
