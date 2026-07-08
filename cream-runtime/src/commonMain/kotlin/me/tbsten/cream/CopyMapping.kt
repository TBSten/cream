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
 * # Excluding auto-copy defaults
 *
 * Use the `excludes` parameter to remove the auto-copy default of generated parameters.
 * Each entry names a generated parameter — i.e. a **target-side** property name (consistent
 * with the generated signature, including `properties` renames). The parameter stays in the
 * signature but becomes required:
 *
 * ```kt
 * @CopyMapping(
 *     source = LibXModel::class,
 *     target = LibYModel::class,
 *     excludes = ["shareProp"],
 * )
 * private object Mapping
 *
 * // auto generate
 * fun LibXModel.copyToLibYModel(
 *     shareProp: String,  // no default — caller must specify
 *     yProp: Int,
 * ): LibYModel = ...
 * ```
 *
 * When `canReverse = true`, `excludes` applies in both directions. Entries are translated
 * through the reversed `properties` mappings: an entry naming the `target` of a
 * [CopyMapping.Map] excludes the source-side parameter in the reverse function; entries
 * without a mapping (same-named shared properties) apply as-is.
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
 * @param excludes Generated parameter names (target-side property names) whose auto-copy default
 *   is removed, making the parameter required. When `canReverse` is true, entries are translated
 *   through the reversed `properties` mappings for the reverse function. An entry that matches no
 *   auto-defaulted parameter has no effect and emits a KSP warning.
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
