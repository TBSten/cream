package me.tbsten.cream.test.combineMapping

import me.tbsten.cream.CombineMapping

/**
 * Library C model (simulates a model from yet another external library)
 * Cannot be modified as it's from a library.
 */
data class LibCModel(
    val nameC: String,
    val valueC: Boolean,
)

/**
 * Target model that combines three source classes
 */
data class TripleCombinedModel(
    val nameA: String,
    val valueA: Int,
    val nameB: String,
    val valueB: Double,
    val nameC: String,
    val valueC: Boolean,
)

/**
 * Combine mapping with three source classes
 *
 * Generates: fun LibAModel.copyToTripleCombinedModel(
 *     libBModel: LibBModel,
 *     libCModel: LibCModel
 * ): TripleCombinedModel
 */
@CombineMapping(
    sources = [LibAModel::class, LibBModel::class, LibCModel::class],
    target = TripleCombinedModel::class,
)
private object MultiSourceMapping
