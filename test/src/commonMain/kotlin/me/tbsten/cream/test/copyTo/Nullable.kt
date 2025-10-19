package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo

// Non-nullable to nullable properties
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
