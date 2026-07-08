package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom
import kotlin.jvm.JvmInline

@JvmInline
value class AutoMappedId(
    val value: String,
)

// Wrap (always on): source `id: String` -> target `id: AutoMappedId` gets `= AutoMappedId(this.id)`.
data class ValueClassWrapSource(
    val id: String,
    val name: String,
)

@CopyFrom(ValueClassWrapSource::class)
data class ValueClassWrapTarget(
    val id: AutoMappedId,
    val name: String,
)

// Unwrap (always on): source `id: AutoMappedId` -> target `id: String` gets `= this.id.value`.
data class ValueClassUnwrapSource(
    val id: AutoMappedId,
    val name: String,
)

@CopyFrom(ValueClassUnwrapSource::class)
data class ValueClassUnwrapTarget(
    val id: String,
    val name: String,
)
