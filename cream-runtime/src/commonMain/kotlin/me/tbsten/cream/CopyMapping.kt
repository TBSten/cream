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
 * # Property Mapping
 *
 * Use the `properties` parameter to map properties with different names:
 *
 * ```kt
 * @CopyMapping(
 *     source = LibXModel::class,
 *     target = LibYModel::class,
 *     properties = [CopyMapping.Map(source = "xProp", target = "yProp")]
 * )
 * private object Mapping
 *
 * // auto generate with property mapping
 * fun LibXModel.copyToLibYModel(
 *     shareProp: String = this.shareProp,
 *     yProp: Int = this.xProp,  // xProp is mapped to yProp
 * ): LibYModel = ...
 * ```
 *
 * # Bidirectional Mapping
 *
 * Set `canReverse = true` to generate copy functions in both directions:
 *
 * ```kt
 * @CopyMapping(LibXModel::class, LibYModel::class, canReverse = true)
 * private object Mapping
 *
 * // auto generate both:
 * fun LibXModel.copyToLibYModel(...): LibYModel = ...
 * fun LibYModel.copyToLibXModel(...): LibXModel = ...
 * ```
 *
 * When using `canReverse` with property mappings, the mappings are automatically reversed:
 *
 * ```kt
 * @CopyMapping(
 *     source = LibXModel::class,
 *     target = LibYModel::class,
 *     canReverse = true,
 *     properties = [CopyMapping.Map(source = "xProp", target = "yProp")]
 * )
 * private object Mapping
 *
 * // Forward: xProp -> yProp
 * // Reverse: yProp -> xProp
 * ```
 *
 * @param source The source class to copy from
 * @param target The target class to copy to
 * @param canReverse If true, also generates a reverse copy function (target -> source). Default is false.
 * @param properties Property mappings that define how to map properties with different names between source and target.
 *
 * @see CopyTo
 * @see CopyFrom
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class CopyMapping(
    val source: KClass<*>,
    val target: KClass<*>,
    val canReverse: Boolean = false,
    val properties: Array<Map> = [],
) {
    /**
     * Defines a property name mapping between source and target classes.
     *
     * This allows you to map properties with different names when generating copy functions.
     *
     * ## Example
     *
     * ```kt
     * @CopyMapping(
     *     source = PersonDto::class,
     *     target = UserEntity::class,
     *     properties = [
     *         CopyMapping.Map(source = "fullName", target = "name"),
     *         CopyMapping.Map(source = "emailAddr", target = "email")
     *     ]
     * )
     *
     * @param source The property name in the source class
     * @param target The property name in the target class
     * ```
     */
    annotation class Map(val source: String, val target: String)
}
