package me.tbsten.cream

import kotlin.reflect.KClass

/**
 * Generate `<annotated by CopyTo class>.copyTo<targets class>()` copy functions.
 *
 * # Example
 *
 * ```kt
 * @CopyTo(State.Success::class)
 * sealed interface State {
 *   val prop1: String
 *   class Success(
 *      val prop1: String,
 *      val prop2: Int,
 *   )
 * }
 *
 * // Auto generate
 *
 * fun State.copyToSuccess(
 *   prop1: String = this.prop1,
 *   prop2: Int,
 * ) = Success(...)
 * ```
 *
 * @see CopyFrom
 */
@Target(AnnotationTarget.CLASS)
annotation class CopyTo(
    vararg val targets: KClass<*>,
) {
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(
        vararg val propertyNames: String,
    )
}
