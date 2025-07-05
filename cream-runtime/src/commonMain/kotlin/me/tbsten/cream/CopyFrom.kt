package me.tbsten.cream

import kotlin.reflect.KClass

/**
 * Generate `<sources class>.copyTo<annotated by CopyFrom class>()` copy functions.
 *
 * # Example
 *
 * ```kt
 * sealed interface State {
 *   val prop1: String
 *   @CopyFrom(State::class)
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
 * @see CopyTo
 */
@Target(AnnotationTarget.CLASS)
annotation class CopyFrom(
    vararg val sources: KClass<*>,
)
