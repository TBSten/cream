package me.tbsten.cream.test.combineMapping

import me.tbsten.cream.CombineMapping

/**
 * Library A model (simulates a model from an external library)
 * Cannot be modified as it's from a library.
 */
data class LibAModel(
    val nameA: String,
    val valueA: Int,
)

/**
 * Library B model (simulates a model from another external library)
 * Cannot be modified as it's from a library.
 */
data class LibBModel(
    val nameB: String,
    val valueB: Double,
)

/**
 * Target model that combines properties from LibA and LibB
 */
data class CombinedModel(
    val nameA: String,
    val valueA: Int,
    val nameB: String,
    val valueB: Double,
    val extraProperty: String,
)

/**
 * Basic combine mapping test
 *
 * Generates: fun LibAModel.copyToCombinedModel(libBModel: LibBModel, extraProperty: String): CombinedModel
 */
@CombineMapping(
    sources = [LibAModel::class, LibBModel::class],
    target = CombinedModel::class
)
private object BasicMapping
