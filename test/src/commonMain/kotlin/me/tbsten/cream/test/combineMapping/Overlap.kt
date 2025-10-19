package me.tbsten.cream.test.combineMapping

import me.tbsten.cream.CombineMapping

/**
 * Source model D for overlap testing
 */
data class LibDModel(
    val sharedName: String,
    val specificD: String,
)

/**
 * Source model E for overlap testing
 */
data class LibEModel(
    val sharedName: String,
    val specificE: Int,
)

/**
 * Target model with overlapping property names
 */
data class OverlapTargetModel(
    val sharedName: String, // Both LibDModel and LibEModel have this property
    val valueA: String,
    val valueB: Int,
)

/**
 * Combine mapping with overlapping property names
 *
 * When multiple sources have the same property name, the last source takes precedence.
 */
@CombineMapping(
    sources = [LibDModel::class, LibEModel::class],
    target = OverlapTargetModel::class,
    properties = [
        CombineMapping.Map(source = "specificD", target = "valueA"),
        CombineMapping.Map(source = "specificE", target = "valueB"),
    ],
)
private object OverlapMapping
