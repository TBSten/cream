package me.tbsten.cream

import kotlin.reflect.KClass

/**
 * Generate `<annotated by CombineTo class>.copyTo<targets class>()` copy functions that combine properties from multiple source classes.
 *
 * # Example
 *
 * ```kt
 * @CombineTo(SuccessState::class)
 * data class LoadingState(
 *     val itemId: String,
 * )
 *
 * @CombineTo(SuccessState::class)
 * data class SuccessAction(
 *     val data: Data,
 * )
 *
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
 * Annotate a **source-class property** with [CombineTo.Exclude] to drop its
 * `= this.<property>` (or `= sourceParam.<property>`) auto-copy default. The matching parameter
 * stays in the generated copy function's signature but becomes required at the call site. See
 * [CombineTo.Exclude] for details and an example.
 *
 * @property visibility Visibility modifier of the generated copy function. Defaults to
 *   [CopyVisibility.INHERIT], which keeps cream's existing behaviour (the function inherits
 *   the target class's visibility).
 * @property funName Template for the generated function name. Defaults to
 *   [DefaultCopyFunctionName] (cream's derived name). Embed naming tokens such as
 *   [CopyTargetSimpleName] to compose a name. When this annotation lists more than one
 *   `targets`, use a token so each generated function gets a distinct name.
 *   See `CopyFunctionNameToken.kt`.
 *
 * @see CopyTo
 * @see CopyFrom
 * @see CombineTo.Exclude
 * @see CopyVisibility
 * @see DefaultCopyFunctionName
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
public annotation class CombineTo(
    vararg val targets: KClass<*>,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
    val funName: String = DefaultCopyFunctionName,
) {
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE_PARAMETER)
    public annotation class Map(
        vararg val propertyNames: String,
    )

    /**
     * Remove the auto-copy default from a matched constructor parameter, making it required.
     *
     * Place this annotation on a **source-class property** that should not be copied
     * automatically. The corresponding parameter in the generated copy function keeps its
     * position but loses the `= this.<property>` (or `= sourceParam.<property>`) default,
     * forcing the caller to provide an explicit value.
     *
     * Applying `@CombineTo.Exclude` to a property that is not matched to any target parameter
     * has no effect and emits a KSP warning.
     *
     * # Example
     *
     * ```kt
     * @CombineTo(SuccessState::class)
     * data class LoadingState(
     *     val itemId: String,
     *     @CombineTo.Exclude val sessionId: String,  // caller must specify sessionId explicitly
     * )
     *
     * // Generated:
     * fun LoadingState.copyToSuccessState(
     *     ...,
     *     itemId: String = this.itemId,
     *     sessionId: String,               // no default — required
     * ): SuccessState = SuccessState(...)
     * ```
     *
     * @see CombineTo.Map
     * @see CombineTo
     */
    @Target(AnnotationTarget.PROPERTY)
    public annotation class Exclude
}
