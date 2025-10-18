package me.tbsten.cream.test.combineTo.edgeCase

import me.tbsten.cream.CombineTo

// Multiple sources (4+) test
@CombineTo(MultiSourceTarget::class)
data class MultiSourceA(
    val propertyA: String,
)

@CombineTo(MultiSourceTarget::class)
data class MultiSourceB(
    val propertyB: Int,
)

@CombineTo(MultiSourceTarget::class)
data class MultiSourceC(
    val propertyC: Boolean,
)

@CombineTo(MultiSourceTarget::class)
data class MultiSourceD(
    val propertyD: Double,
)

data class MultiSourceTarget(
    val propertyA: String,
    val propertyB: Int,
    val propertyC: Boolean,
    val propertyD: Double,
)
