package me.tbsten.cream.test.copyToChildren

import me.tbsten.cream.CopyToChildren

// Complex types
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
