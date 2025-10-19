package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo

// Nested classes
@CopyTo(NestedTarget::class)
data class NestedSource(
    val parent: ParentClass,
)

data class NestedTarget(
    val parent: ParentClass,
    val newProperty: String,
)

data class ParentClass(
    val name: String,
    val value: Int,
)
