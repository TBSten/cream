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
 * # Exclude
 *
 * Because the source and target classes live in libraries you cannot annotate, `@CopyTo.Exclude` /
 * `@CopyFrom.Exclude` are not available here. Use the `excludes` parameter instead: each entry names a
 * generated (target-side) parameter whose `= this.<property>` auto-copy default should be dropped, making it
 * required — the annotation-level equivalent of `@Exclude`.
 *
 * ```kt
 * @CopyMapping(
 *     source = LibXModel::class,
 *     target = LibYModel::class,
 *     excludes = ["shareProp"],  // drop the auto-default for shareProp -> now required
 * )
 * private object Mapping
 *
 * // auto generate
 * fun LibXModel.copyToLibYModel(
 *     shareProp: String,          // no `= this.shareProp` default — required
 *     yProp: Int,
 * ): LibYModel = ...
 * ```
 *
 * An `excludes` entry that matches no auto-defaulted parameter has no effect and emits a KSP warning.
 * `excludes` reference target-side names; a renamed-then-excluded property is fine
 * (`properties = [Map(source = "xProp", target = "yProp")]` + `excludes = ["yProp"]`).
 *
 * @param source The source class to copy from
 * @param target The target class to copy to
 * @param canReverse If true, also generates a reverse copy function (target -> source). Default is false.
 * @param properties Property mappings that define how to map properties with different names between source and target.
 * @param excludes Names of generated (target-side) parameters whose auto-copy default should be dropped, making
 *   them required. The annotation-level equivalent of `@Exclude` for external classes. Unmatched entries emit a
 *   KSP warning.
 * @param funName Template for the generated function name. Defaults to [DefaultCopyFunctionName]
 *   (cream's derived name). Embed naming tokens such as [CopyTargetSimpleName] to compose a name.
 *   When `canReverse` is true (or the target is sealed) use a token so the forward and reverse
 *   functions get distinct names. See `CopyFunctionNameToken.kt`.
 * @param visibility Visibility modifier of the generated copy function. Defaults to
 *   [CopyVisibility.INHERIT], which keeps cream's existing behaviour (the function inherits
 *   the target class's visibility). When `canReverse` is true, the same visibility is applied
 *   to both the forward and reverse functions.
 *
 * @see CopyTo
 * @see CopyFrom
 * @see CopyVisibility
 * @see DefaultCopyFunctionName
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
public annotation class CopyMapping(
    val source: KClass<*>,
    val target: KClass<*>,
    val canReverse: Boolean = false,
    val properties: Array<Map> = [],
    val excludes: Array<String> = [],
    val kdoc: KDoc = KDoc(),
    val funName: String = DefaultCopyFunctionName,
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
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
    @Target()
    public annotation class Map(
        val source: String,
        val target: String,
    )
}
