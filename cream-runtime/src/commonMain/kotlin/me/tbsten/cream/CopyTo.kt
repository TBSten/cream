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
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
@Repeatable
annotation class CopyTo(
    vararg val targets: KClass<*>,
) {
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(
        vararg val propertyNames: String,
    )

    /**
     * Generate copy function that uses a factory function instead of constructor.
     *
     * # Example
     *
     * ```kt
     * @CopyTo.Fun(funName = "createMyTarget")
     * data class MySource(val a: String)
     *
     * fun createMyTarget(a: String, b: Int): MyTarget = MyTarget(a, b)
     *
     * // Auto generate
     * fun MySource.copyToMyTarget(
     *   a: String = this.a,
     *   b: Int,
     * ) = createMyTarget(a = a, b = b)
     * ```
     *
     * You can also use companion object factory methods:
     *
     * ```kt
     * @CopyTo.Fun(funName = "MyTarget.from")
     * data class MySource(val a: String)
     *
     * data class MyTarget(val a: String, val b: Int) {
     *   companion object {
     *     fun from(a: String, b: Int) = MyTarget(a, b)
     *   }
     * }
     * ```
     */
    @Target(AnnotationTarget.CLASS)
    @Repeatable
    annotation class Fun(
        val funName: String,
    )
}
