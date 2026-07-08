package me.tbsten.cream.test.copyMapping

import me.tbsten.cream.CopyMapping
import kotlin.jvm.JvmInline

@JvmInline
value class MappedId(
    val value: String,
)

// Simulates an external library model exposing the raw type...
data class RawIdModel(
    val id: String,
    val name: String,
)

// ...and a domain model wrapping it in a value class.
data class WrappedIdModel(
    val id: MappedId,
    val name: String,
)

// canReverse: the forward function wraps (`MappedId(this.id)`), the generated reverse function
// unwraps (`this.id.value`).
@CopyMapping(RawIdModel::class, WrappedIdModel::class, canReverse = true)
private object ValueClassMappingHolder
