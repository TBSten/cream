package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

// Nested classes
@CopyFrom(NestedSource::class)
data class NestedTarget(
    val parent: ParentClass,
    val newProperty: String,
)

data class NestedSource(
    val parent: ParentClass,
)

data class ParentClass(
    val name: String,
    val value: Int,
)
