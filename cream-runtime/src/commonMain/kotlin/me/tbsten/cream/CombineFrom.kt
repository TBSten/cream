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
 * @property visibility Visibility modifier of the generated copy function. Defaults to
 *   [CopyVisibility.INHERIT], which keeps cream's existing behaviour (the function inherits
 *   the target class's visibility).
 *
 * @see CombineTo
 * @see CopyFrom
 * @see CopyVisibility
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
@Repeatable
annotation class CombineFrom(
    vararg val sources: KClass<*>,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
) {
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(
        vararg val propertyNames: String,
    )
}
