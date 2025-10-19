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
 * @see CopyTo
 * @see CopyFrom
 */
@Target(AnnotationTarget.CLASS)
annotation class CombineTo(
    vararg val targets: KClass<*>,
) {
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(
        vararg val propertyNames: String,
    )
}
