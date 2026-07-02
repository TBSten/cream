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
 * # Exclude
 *
 * Because the source and target classes live in libraries you cannot annotate, `@CombineTo.Exclude` /
 * `@CombineFrom.Exclude` are not available here. Use the `excludes` parameter instead: each entry names a
 * generated (target-side) parameter whose `= this.<property>` / `= <source>.<property>` auto-copy default should
 * be dropped, making it required — the annotation-level equivalent of `@Exclude`.
 *
 * ```kt
 * @CombineMapping(
 *     sources = [LibAModel::class, LibBModel::class],
 *     target = LibCModel::class,
 *     excludes = ["propA"],  // drop the auto-default for propA -> now required
 * )
 * private object Mapping
 * ```
 *
 * `excludes` entries are **always target-side names** (the combine-destination constructor parameter),
 * even when `properties` renames the matching source property.
 *
 * An `excludes` entry that matches no auto-defaulted parameter has no effect and emits a KSP warning.
 *
 * @param sources The source classes to combine from (must have at least 2 sources)
 * @param target The target class to combine to
 * @param properties Property mappings that define how to map properties with different names between sources and target.
 * @param excludes Names of generated parameters whose auto-copy default should be dropped, making them
 *   required. Always specify the **target-side** (combine-destination constructor parameter) name. The
 *   annotation-level equivalent of `@Exclude` for external classes. Unmatched entries emit a KSP warning.
 * @param visibility Visibility modifier of the generated copy function. Defaults to
 *   [CopyVisibility.INHERIT], which keeps cream's existing behaviour (the function inherits
 *   the target class's visibility).
 * @param funName Template for the generated function name. Defaults to [DefaultCopyFunctionName]
 *   (cream's derived name). Embed naming tokens such as [CopyTargetSimpleName] to compose a name,
 *   or pass a plain literal. `@CombineMapping` always generates a single function.
 *   See `CopyFunctionNameToken.kt`.
 *
 * @see CombineTo
 * @see CombineFrom
 * @see CopyMapping
 * @see CopyVisibility
 * @see DefaultCopyFunctionName
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
public annotation class CombineMapping(
    val sources: Array<KClass<*>>,
    val target: KClass<*>,
    val properties: Array<Map> = [],
    val excludes: Array<String> = [],
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
    val funName: String = DefaultCopyFunctionName,
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
    @Target()
    public annotation class Map(
        val source: String,
        val target: String,
    )
}
