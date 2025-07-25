package me.tbsten.cream.test.copyTo.edgeCase

import me.tbsten.cream.CopyTo

// 1. data object -> data object copy
@CopyTo(EmptyTarget::class)
data object EmptySource

data object EmptyTarget

// 2. Non-nullable to nullable properties
@CopyTo(NullableTarget::class)
data class NonNullableSource(
    val str: String,
    val num: Int,
    val bool: Boolean,
    val list: List<String>,
)

data class NullableTarget(
    val str: String?,
    val num: Int?,
    val bool: Boolean?,
    val list: List<String>?,
    val newProperty: String,
)

// 3. Nested classes
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

// 4. Complex types
@CopyTo(ComplexTypeTarget::class)
data class ComplexTypeSource(
    val stringList: List<String>,
    val intMap: Map<String, Int>,
    val nullableSet: Set<Double>?,
)

data class ComplexTypeTarget(
    val stringList: List<String>,
    val intMap: Map<String, Int>,
    val nullableSet: Set<Double>?,
    val newProperty: String,
)

// 5. Different visibility properties
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

// 6. Property mapping with @CopyTo.Property
@CopyTo(DataTargetModel::class)
data class DomainSourceModel(
    @CopyTo.Property("dataId")
    val domainId: String,
    val name: String,
)

data class DataTargetModel(
    val dataId: String,
    val name: String,
) 