package me.tbsten.cream.test.combineFrom

import me.tbsten.cream.CombineFrom

data class SourceStateA(
    val propertyA: String,
)

data class SourceStateB(
    val propertyB: Int,
)

@CombineFrom(SourceStateA::class, SourceStateB::class)
data class TargetState(
    val propertyA: String,
    val propertyB: Int,
    val propertyC: Boolean,
)
