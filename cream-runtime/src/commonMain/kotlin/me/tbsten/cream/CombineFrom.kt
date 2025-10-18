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
 * @see CombineTo
 * @see CopyFrom
 */
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class CombineFrom(
    vararg val sources: KClass<*>,
) {
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(vararg val propertyNames: String)
}
