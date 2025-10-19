package me.tbsten.cream.test.copyToChildren.edgeCase

import me.tbsten.cream.CopyToChildren

// 1. data object -> data object copy
@CopyToChildren
sealed class EmptyParent

data object EmptyChild1 : EmptyParent()

data object EmptyChild2 : EmptyParent()

// 2. Nested classes
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

// 3. Complex types
@CopyToChildren
sealed class ComplexTypeParent {
    abstract val stringList: List<String>?
    abstract val intMap: Map<String, Int>?
}

data class ComplexTypeChild1(
    override val stringList: List<String>,
    override val intMap: Map<String, Int>,
) : ComplexTypeParent()

data class ComplexTypeChild2(
    override val stringList: List<String>?,
    override val intMap: Map<String, Int>?,
) : ComplexTypeParent()

// 4. Different visibility properties
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
