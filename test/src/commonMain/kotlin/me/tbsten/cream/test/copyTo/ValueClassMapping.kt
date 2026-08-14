package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo
import kotlin.jvm.JvmInline

@JvmInline
value class AutoMappedId(
    val value: String,
)

// Wrap (always on): source `id: String` -> target `id: AutoMappedId` gets `= AutoMappedId(this.id)`.
@CopyTo(ValueClassWrapTarget::class)
data class ValueClassWrapSource(
    val id: String,
    val name: String,
)

data class ValueClassWrapTarget(
    val id: AutoMappedId,
    val name: String,
)

// Unwrap (always on): source `id: AutoMappedId` -> target `id: String` gets `= this.id.value`.
@CopyTo(ValueClassUnwrapTarget::class)
data class ValueClassUnwrapSource(
    val id: AutoMappedId,
    val name: String,
)

data class ValueClassUnwrapTarget(
    val id: String,
    val name: String,
)
