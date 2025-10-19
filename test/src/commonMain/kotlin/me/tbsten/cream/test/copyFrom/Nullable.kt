package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

// Non-nullable to nullable properties
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
