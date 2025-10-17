package me.tbsten.cream

import kotlin.reflect.KClass

/**
 * Generate copy functions between two classes without modifying either class.
 *
 * This is useful when you want to generate copy functions between library classes
 * that you cannot modify directly.
 *
 * # Example
 *
 * ```kt
 * // in library X
 * data class LibXModel(
 *     val shareProp: String,
 *     val xProp: Int,
 * )
 *
 * // in library Y
 * data class LibYModel(
 *     val shareProp: String,
 *     val yProp: Int,
 * )
 *
 * // in your module
 * @CopyMapping(LibXModel::class, LibYModel::class)
 * private object Mapping
 *
 * // auto generate
 * fun LibXModel.copyToLibYModel(
 *     shareProp: String = this.shareProp,
 *     yProp: Int,
 * ): LibYModel = ...
 * ```
 *
 * @param source The source class to copy from
 * @param target The target class to copy to
 *
 * @see CopyTo
 * @see CopyFrom
 */
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class CopyMapping(
    val source: KClass<*>,
    val target: KClass<*>,
)
