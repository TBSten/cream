package me.tbsten.cream.test.combineFrom.edgeCase

import me.tbsten.cream.CombineFrom

/**
 * Test generic type parameters with @CombineFrom
 */

data class GenericSourceA<T>(
    val genericProperty: T,
    val normalProperty: String,
)

data class GenericSourceB<U>(
    val anotherGeneric: U,
)

@CombineFrom(GenericSourceA::class, GenericSourceB::class)
data class GenericTarget<T, U>(
    val genericProperty: T,
    val normalProperty: String,
    val anotherGeneric: U,
    val extraProperty: Int,
)
