package me.tbsten.cream.test.combineMapping

import me.tbsten.cream.CombineMapping

/**
 * Library model used (via a typealias) as a @CombineMapping source.
 */
data class LibAliasFirstModel(
    val firstName: String,
    val firstValue: Int,
)

/**
 * Library model used (via a typealias) as a @CombineMapping source.
 */
data class LibAliasSecondModel(
    val secondName: String,
    val secondValue: Double,
)

/**
 * Target model that combines both aliased sources, referenced via a typealias.
 */
data class LibAliasCombinedModel(
    val firstName: String,
    val firstValue: Int,
    val secondName: String,
    val secondValue: Double,
    val extraProperty: String,
)

typealias LibAliasFirstSource = LibAliasFirstModel

typealias LibAliasSecondSource = LibAliasSecondModel

typealias LibAliasCombinedTarget = LibAliasCombinedModel

/**
 * Mapping object whose @CombineMapping sources and target are all type aliases.
 *
 * Generates: fun LibAliasFirstModel.copyToLibAliasCombinedModel(
 *     libAliasSecondModel: LibAliasSecondModel, extraProperty: String,
 * ): LibAliasCombinedModel
 */
@CombineMapping(
    sources = [LibAliasFirstSource::class, LibAliasSecondSource::class],
    target = LibAliasCombinedTarget::class,
)
private object AliasCombineMapping
