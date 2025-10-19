package me.tbsten.cream.test.copyToChildren

import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface CopyToChildrenExcludeTestParent {
    val sharedProp: String

    @CopyToChildren.Exclude
    val excludedProp: String

    data class Child(
        override val sharedProp: String,
        override val excludedProp: String,
        val childProp: String,
    ) : CopyToChildrenExcludeTestParent
}
