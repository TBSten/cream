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
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
annotation class CombineTo(
    vararg val targets: KClass<*>,
) {
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(
        vararg val propertyNames: String,
    )

    /**
     * Generate combine copy function that uses a factory function instead of constructor.
     *
     * # Example
     *
     * ```kt
     * @CombineTo.Fun(funName = "createSuccessState")
     * data class LoadingState(val itemId: String)
     *
     * @CombineTo.Fun(funName = "createSuccessState")
     * data class SuccessAction(val data: Data)
     *
     * data class SuccessState(
     *     val itemId: String,
     *     val data: Data,
     *     val lastUpdateAt: Date,
     * )
     *
     * fun createSuccessState(
     *     itemId: String,
     *     data: Data,
     *     lastUpdateAt: Date
     * ): SuccessState = SuccessState(itemId, data, lastUpdateAt)
     *
     * // Auto generate
     * fun LoadingState.copyToSuccessState(
     *     successAction: SuccessAction,
     *     itemId: String = this.itemId,
     *     data: Data = successAction.data,
     *     lastUpdateAt: Date,
     * ) = createSuccessState(itemId = itemId, data = data, lastUpdateAt = lastUpdateAt)
     * ```
     */
    @Target(AnnotationTarget.CLASS)
    @Repeatable
    annotation class Fun(
        val funName: String,
    )
}
