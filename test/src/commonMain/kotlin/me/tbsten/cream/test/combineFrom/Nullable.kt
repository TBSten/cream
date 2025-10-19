package me.tbsten.cream.test.combineFrom

import me.tbsten.cream.CombineFrom

/**
 * Test nullable properties with @CombineFrom
 */

data class NullableSourceA(
    val nullableProperty: String?,
    val nonNullProperty: String,
)

data class NullableSourceB(
    val anotherNullable: Int?,
)

@CombineFrom(NullableSourceA::class, NullableSourceB::class)
data class NullableTarget(
    val nullableProperty: String?,
    val nonNullProperty: String,
    val anotherNullable: Int?,
    val extraProperty: Boolean,
)
