package me.tbsten.cream.test.combineFrom.edgeCase

import me.tbsten.cream.CombineFrom

/**
 * Test combining to object targets with @CombineFrom
 */

data class ObjectSourceA(
    val propertyA: String,
)

data class ObjectSourceB(
    val propertyB: Int,
)

@CombineFrom(ObjectSourceA::class, ObjectSourceB::class)
object TargetObject
