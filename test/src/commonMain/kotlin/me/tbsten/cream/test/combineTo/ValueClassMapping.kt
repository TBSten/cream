package me.tbsten.cream.test.combineTo

import me.tbsten.cream.CombineTo
import kotlin.jvm.JvmInline

@JvmInline
value class CombinedId(
    val value: String,
)

// Wrap from the receiver source (`= CombinedId(this.id)`) and unwrap from the secondary source
// (`= combineSourceB.serial.value`) in the same generated combine function.
@CombineTo(CombineValueClassTarget::class)
data class CombineSourceA(
    val id: String,
)

@CombineTo(CombineValueClassTarget::class)
data class CombineSourceB(
    val serial: CombinedId,
)

data class CombineValueClassTarget(
    val id: CombinedId,
    val serial: String,
    val extra: Boolean,
)
