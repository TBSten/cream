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
    val zProp: Int,
)

/**
 * Mapping object that generates copy functions between library classes
 * without modifying the library classes themselves.
 *
 * Example: LibXModel.xProp maps to LibYModel.yProp via property mapping
 */
@CopyMapping(
    LibXModel::class,
    LibYModel::class,
    properties = [CopyMapping.Map(source = "xProp", target = "yProp")],
)
@CopyMapping(LibYModel::class, LibZModel::class)
private object Mapping

/**
 * Library W model for testing bidirectional mapping
 *
 * @property shareProp Shared property
 * @property wProp Property specific to LibW
 */
data class LibWModel(
    val shareProp: String,
    val wProp: Double,
)

/**
 * Library V model for testing bidirectional mapping
 *
 * @property shareProp Shared property
 * @property vProp Property specific to LibV
 */
data class LibVModel(
    val shareProp: String,
    val vProp: Boolean,
)

/**
 * Mapping object with canReverse = true to generate bidirectional copy functions
 */
@CopyMapping(LibWModel::class, LibVModel::class, canReverse = true)
private object ReversibleMapping
