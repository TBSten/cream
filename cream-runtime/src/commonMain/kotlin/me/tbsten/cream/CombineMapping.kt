package me.tbsten.cream

import kotlin.reflect.KClass

/**
 * Generate combine copy functions between multiple source classes and a target class without modifying any class.
 *
 * This is useful when you want to generate combine copy functions between library classes
 * that you cannot modify directly. It's similar to [CombineTo] and [CombineFrom], but works
 * with classes you don't control.
 *
 * # Example
 *
 * ```kt
 * // in library A
 * data class LibAModel(
 *     val propA: String,
 *     val valueA: Int,
 * )
 *
 * // in library B
 * data class LibBModel(
 *     val propB: String,
 *     val valueB: Double,
 * )
 *
 * // in library C
 * data class LibCModel(
 *     val propA: String,
 *     val valueA: Int,
 *     val propB: String,
 *     val valueB: Double,
 *     val extra: String,
 * )
 *
 * // in your module
 * @CombineMapping(
 *     sources = [LibAModel::class, LibBModel::class],
 *     target = LibCModel::class
 * )
 * private object Mapping
 *
 * // auto generate
 * fun LibAModel.copyToLibCModel(
 *     libBModel: LibBModel,
 *     propA: String = this.propA,
 *     valueA: Int = this.valueA,
 *     propB: String = libBModel.propB,
 *     valueB: Double = libBModel.valueB,
 *     extra: String,
 * ): LibCModel = ...
 * ```
 *
 * # Property Mapping
 *
 * Use the `properties` parameter to map properties with different names:
 *
 * ```kt
 * @CombineMapping(
 *     sources = [LibAModel::class, LibBModel::class],
 *     target = LibCModel::class,
 *     properties = [
 *         CombineMapping.Map(source = "propA", target = "targetPropA"),
 *         CombineMapping.Map(source = "propB", target = "targetPropB")
 *     ]
 * )
 * private object Mapping
 *
 * // auto generate with property mapping
 * fun LibAModel.copyToLibCModel(
 *     libBModel: LibBModel,
 *     targetPropA: String = this.propA,  // propA is mapped to targetPropA
 *     valueA: Int = this.valueA,
 *     targetPropB: String = libBModel.propB,  // propB is mapped to targetPropB
 *     valueB: Double = libBModel.valueB,
 *     extra: String,
 * ): LibCModel = ...
 * ```
 *
 * @param sources The source classes to combine from (must have at least 2 sources)
 * @param target The target class to combine to
 * @param properties Property mappings that define how to map properties with different names between sources and target.
 *
 * @see CombineTo
 * @see CombineFrom
 * @see CopyMapping
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class CombineMapping(
    val sources: Array<KClass<*>>,
    val target: KClass<*>,
    val properties: Array<Map> = [],
) {
    /**
     * Defines a property name mapping between a source class and the target class.
     *
     * This allows you to map properties with different names when generating combine copy functions.
     *
     * @param source The property name in one of the source classes
     * @param target The property name in the target class
     *
     * ## Example Usage
     *
     * ```kt
     * @CombineMapping(
     *     sources = [PersonDto::class, AddressDto::class],
     *     target = UserEntity::class,
     *     properties = [
     *         CombineMapping.Map(source = "fullName", target = "name"),
     *         CombineMapping.Map(source = "streetAddr", target = "street")
     *     ]
     * )
     * ```
     */
    annotation class Map(
        val source: String,
        val target: String,
    )
}
