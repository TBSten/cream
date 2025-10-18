package me.tbsten.cream.test.combineTo.edgeCase

import me.tbsten.cream.CombineTo

// Object target test
@CombineTo(TargetObject::class)
data class ObjectSourceA(
    val propertyA: String,
)

@CombineTo(TargetObject::class)
data class ObjectSourceB(
    val propertyB: Int,
)

object TargetObject
