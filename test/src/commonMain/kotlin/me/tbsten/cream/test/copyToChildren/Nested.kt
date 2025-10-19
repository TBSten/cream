package me.tbsten.cream.test.copyToChildren

import me.tbsten.cream.CopyToChildren

// Nested classes
@CopyToChildren
sealed class NestedParent {
    open val parent: ParentClass = ParentClass("parent", 0)
}

data class NestedChild1(
    override val parent: ParentClass,
    val name: String,
) : NestedParent()

data class NestedChild2(
    override val parent: ParentClass,
    val value: Int,
) : NestedParent()

data class ParentClass(
    val name: String,
    val value: Int,
)
