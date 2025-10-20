package me.tbsten.cream.test.copyToFun

import me.tbsten.cream.CopyTo

// Nullable properties with factory function
@CopyTo.Fun(funName = "createNullableTarget")
data class NullableSource(
    val required: String,
    val optional: String?,
)

data class NullableTarget(
    val required: String,
    val optional: String?,
    val extra: Int?,
)

fun createNullableTarget(
    required: String,
    optional: String?,
    extra: Int?,
): NullableTarget = NullableTarget(required = required, optional = optional, extra = extra)
