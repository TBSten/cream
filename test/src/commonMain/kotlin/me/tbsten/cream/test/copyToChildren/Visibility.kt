package me.tbsten.cream.test.copyToChildren

import me.tbsten.cream.CopyToChildren

// Different visibility properties
@CopyToChildren
sealed class VisibilityParent {
    abstract val publicProp: String
    internal abstract val internalProp: String
}

data class VisibilityChild1(
    override val publicProp: String,
    internal override val internalProp: String,
) : VisibilityParent()

data class VisibilityChild2(
    override val publicProp: String,
    internal override val internalProp: String,
    val newProperty: String,
) : VisibilityParent()
