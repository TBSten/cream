package me.tbsten.cream.test.combineTo

import me.tbsten.cream.CombineTo

@CombineTo(TargetState::class)
data class SourceStateA(
    val propertyA: String,
)

@CombineTo(TargetState::class)
data class SourceStateB(
    val propertyB: Int,
)

data class TargetState(
    val propertyA: String,
    val propertyB: Int,
    val propertyC: Boolean,
)
