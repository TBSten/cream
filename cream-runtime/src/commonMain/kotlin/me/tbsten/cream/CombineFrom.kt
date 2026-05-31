package me.tbsten.cream

import kotlin.reflect.KClass

/**
 * Generate `<first source class>.copyTo<annotated target class>()` copy functions that combine properties from multiple source classes.
 *
 * This is the inverse of [CombineTo]: while [CombineTo] is placed on source classes pointing to targets,
 * [CombineFrom] is placed on the target class pointing to sources.
 *
 * # Example
 *
 * ```kt
 * data class LoadingState(
 *     val itemId: String,
 * )
 *
 * data class SuccessAction(
 *     val data: Data,
 * )
 *
 * @CombineFrom(LoadingState::class, SuccessAction::class)
 * data class SuccessState(
 *     val itemId: String, // from LoadingState.itemId
 *     val data: Data, // from SuccessAction.data
 *     val lastUpdateAt: Date,
 * )
 *
 * // Auto generate
 * fun LoadingState.copyToSuccessState(
 *     successAction: SuccessAction,
 *     itemId: String = this.itemId,
 *     data: Data = successAction.data,
 *     lastUpdateAt: Date,
 * ): SuccessState = SuccessState(...)
 * ```
 *
 * # Exclude
 *
 * Annotate a **target-class constructor parameter** with [CombineFrom.Exclude] to drop its
 * `= this.<property>` (or `= sourceParam.<property>`) auto-copy default. The matching parameter
 * stays in the generated copy function's signature but becomes required at the call site. See
 * [CombineFrom.Exclude] for details and an example.
 *
 * @property visibility Visibility modifier of the generated copy function. Defaults to
 *   [CopyVisibility.INHERIT], which keeps cream's existing behaviour (the function inherits
 *   the target class's visibility).
 * @property funName Template for the generated function name. Defaults to
 *   [DefaultCopyFunctionName] (cream's derived name). Embed naming tokens such as
 *   [CopyTargetSimpleName] to compose a name, or pass a plain literal for a fixed name.
 *   `@CombineFrom` always generates a single function. See `CopyFunctionNameToken.kt`.
 *
 * @see CombineTo
 * @see CopyFrom
 * @see CombineFrom.Exclude
 * @see CopyVisibility
 * @see DefaultCopyFunctionName
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
@Repeatable
annotation class CombineFrom(
    vararg val sources: KClass<*>,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
    val funName: String = DefaultCopyFunctionName,
) {
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(
        vararg val propertyNames: String,
    )

    /**
     * Remove the auto-copy default from a matched constructor parameter, making it required.
     *
     * Place this annotation on a **target-class constructor parameter** that should not receive
     * the auto-copied value from any source. The parameter keeps its position in the generated
     * copy function's signature but loses the `= this.<property>` / `= sourceParam.<property>`
     * default, forcing the caller to provide an explicit value.
     *
     * Repeated `@CombineFrom` annotations are merged into a single generated function, so
     * `@CombineFrom.Exclude` on a parameter is read once and applies to that one function.
     *
     * Applying `@CombineFrom.Exclude` to a parameter that is not matched to any source property
     * has no effect and emits a KSP warning.
     *
     * # Example
     *
     * ```kt
     * @CombineFrom(LoadingState::class, SuccessAction::class)
     * data class SuccessState(
     *     val itemId: String,
     *     @CombineFrom.Exclude val data: Data,  // caller must specify data explicitly
     *     val lastUpdateAt: Date,
     * )
     *
     * // Generated:
     * fun LoadingState.copyToSuccessState(
     *     ...,
     *     itemId: String = this.itemId,
     *     data: Data,                            // no default — required
     *     lastUpdateAt: Date,
     * ): SuccessState = SuccessState(...)
     * ```
     *
     * @see CombineFrom.Map
     * @see CombineFrom
     */
    @Target(AnnotationTarget.VALUE_PARAMETER)
    annotation class Exclude
}
