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
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
annotation class CopyFrom(
    vararg val sources: KClass<*>,
) {
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(
        vararg val propertyNames: String,
    )

    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
    annotation class Exclude(
        val value: String,
    )

    /**
     * Generate copy function that uses a factory function instead of constructor.
     *
     * # Example
     *
     * ```kt
     * data class MySource(val a: String)
     *
     * @CopyFrom.Fun(funName = "createMyTarget")
     * data class MyTarget(val a: String, val b: Int)
     *
     * fun createMyTarget(a: String, b: Int): MyTarget = MyTarget(a, b)
     *
     * // Auto generate
     * fun MySource.copyToMyTarget(
     *   a: String = this.a,
     *   b: Int,
     * ) = createMyTarget(a = a, b = b)
     * ```
     */
    @Target(AnnotationTarget.CLASS)
    @Repeatable
    annotation class Fun(
        val funName: String,
    )
}
