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
 * @see CopyVisibility
 * @see DefaultCopyFunctionName
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
annotation class CombineTo(
    vararg val targets: KClass<*>,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
    val funName: String = DefaultCopyFunctionName,
) {
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(
        vararg val propertyNames: String,
    )
}
