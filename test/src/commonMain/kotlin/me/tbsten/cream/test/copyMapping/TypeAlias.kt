package me.tbsten.cream.test.copyMapping

import me.tbsten.cream.CopyMapping

/**
 * Library model used as the source of a typealias-based @CopyMapping.
 *
 * @property shareProp Shared property between the aliased source and target
 * @property aProp Property specific to LibAliasAModel
 */
data class LibAliasAModel(
    val shareProp: String,
    val aProp: Int,
)

/**
 * Library model used as the target of a typealias-based @CopyMapping.
 *
 * @property shareProp Shared property between the aliased source and target
 * @property bProp Property specific to LibAliasBModel
 */
data class LibAliasBModel(
    val shareProp: String,
    val bProp: Int,
)

typealias LibAliasASource = LibAliasAModel

typealias LibAliasBTarget = LibAliasBModel

/**
 * Mapping object whose @CopyMapping source and target are both type aliases.
 *
 * The generated function name is based on the resolved class (LibAliasBModel),
 * not the alias (LibAliasBTarget): LibAliasAModel.copyToLibAliasBModel(...).
 */
@CopyMapping(LibAliasASource::class, LibAliasBTarget::class)
private object AliasMapping
