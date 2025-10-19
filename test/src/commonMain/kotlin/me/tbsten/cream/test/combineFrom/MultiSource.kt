package me.tbsten.cream.test.combineFrom

import me.tbsten.cream.CombineFrom

/**
 * Test combining from multiple source classes (4 sources)
 */

data class MultiSourceA(
    val propertyA: String,
)

data class MultiSourceB(
    val propertyB: Int,
)

data class MultiSourceC(
    val propertyC: Boolean,
)

data class MultiSourceD(
    val propertyD: Double,
)

@CombineFrom(
    MultiSourceA::class,
    MultiSourceB::class,
    MultiSourceC::class,
    MultiSourceD::class,
)
data class MultiSourceTarget(
    val propertyA: String,
    val propertyB: Int,
    val propertyC: Boolean,
    val propertyD: Double,
    val extraProperty: String,
)
