package me.tbsten.cream.test.copyFrom.edgeCase

import me.tbsten.cream.CopyFrom

// 1. data object -> data object copy
@CopyFrom(EmptySource::class)
data object EmptyTarget

data object EmptySource

// 2. Non-nullable to nullable properties
@CopyFrom(NonNullableSource::class)
data class NullableTarget(
    val str: String?,
    val num: Int?,
    val bool: Boolean?,
    val list: List<String>?,
    val newProperty: String,
)

data class NonNullableSource(
    val str: String,
    val num: Int,
    val bool: Boolean,
    val list: List<String>,
)

// 3. Nested classes
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

// 4. Complex types
@CopyFrom(ComplexTypeSource::class)
data class ComplexTypeTarget(
    val stringList: List<String>,
    val intMap: Map<String, Int>,
    val nullableSet: Set<Double>?,
    val newProperty: String,
)

data class ComplexTypeSource(
    val stringList: List<String>,
    val intMap: Map<String, Int>,
    val nullableSet: Set<Double>?,
)

// 5. Different visibility properties
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

// 6. Property mapping with @CopyFrom.Map
@CopyFrom(DataModel::class)
data class DomainModel(
    @CopyFrom.Map("dataId")
    val domainId: String,
    val name: String,
)

data class DataModel(
    val dataId: String,
    val name: String,
)
