package me.tbsten.cream.test.copyMapping

import me.tbsten.cream.CopyMapping

/**
 * Library X model (simulates a model from an external library)
 *
 * @property shareProp Shared property between LibX and LibY
 * @property xProp Property specific to LibX
 */
data class LibXModel(
    val shareProp: String,
    val xProp: Int,
)

/**
 * Library Y model (simulates a model from another external library)
 *
 * @property shareProp Shared property between LibX and LibY
 * @property yProp Property specific to LibY
 */
data class LibYModel(
    val shareProp: String,
    val yProp: Int,
)

/**
 * Another Library model for testing multiple mappings
 *
 * @property shareProp Shared property
 * @property zProp Property specific to LibZ
 */
data class LibZModel(
    val shareProp: String,
    val zProp: String,
)

/**
 * Mapping object that generates copy functions between library classes
 * without modifying the library classes themselves.
 */
@CopyMapping(LibXModel::class, LibYModel::class)
@CopyMapping(LibYModel::class, LibZModel::class)
private object Mapping
