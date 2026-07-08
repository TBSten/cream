package me.tbsten.cream.test.combineMapping

import me.tbsten.cream.CombineMapping

/**
 * First source model for testing `excludes` (simulates a model from an external library)
 */
data class ExcludePartA(
    val shareProp: String,
    val aProp: Int,
)

/**
 * Second source model for testing `excludes`
 */
data class ExcludePartB(
    val bProp: Boolean,
)

/**
 * Target model combining ExcludePartA and ExcludePartB
 */
data class ExcludeCombined(
    val shareProp: String,
    val aProp: Int,
    val bProp: Boolean,
)

/**
 * `excludes = ["shareProp"]` drops the auto-copy default of `shareProp`, making it a
 * required parameter of the generated combine function.
 */
@CombineMapping(
    sources = [ExcludePartA::class, ExcludePartB::class],
    target = ExcludeCombined::class,
    excludes = ["shareProp"],
)
private object ExcludeMapping
