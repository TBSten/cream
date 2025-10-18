package me.tbsten.cream.test.combineMapping

import me.tbsten.cream.CombineMapping

/**
 * Target model with property name differences from source models
 */
data class RenamedTargetModel(
    val targetNameA: String,  // maps from LibAModel.nameA
    val targetValueA: Int,     // maps from LibAModel.valueA
    val targetNameB: String,   // maps from LibBModel.nameB
    val targetValueB: Double,  // maps from LibBModel.valueB
    val extra: String,
)

/**
 * Combine mapping with property name mapping
 *
 * Maps properties with different names using CombineMapping.Map
 */
@CombineMapping(
    sources = [LibAModel::class, LibBModel::class],
    target = RenamedTargetModel::class,
    properties = [
        CombineMapping.Map(source = "nameA", target = "targetNameA"),
        CombineMapping.Map(source = "valueA", target = "targetValueA"),
        CombineMapping.Map(source = "nameB", target = "targetNameB"),
        CombineMapping.Map(source = "valueB", target = "targetValueB"),
    ]
)
private object PropertyMappingMapping
