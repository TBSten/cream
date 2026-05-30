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
 * @property visibility Visibility modifier of the generated copy function. Defaults to
 *   [CopyVisibility.INHERIT], which keeps cream's existing behaviour (the function inherits
 *   the target class's visibility).
 * @property funName Template for the generated function name. Defaults to
 *   [DefaultCopyFunctionName] (cream's derived name). Embed naming tokens such as
 *   [CopyTargetSimpleName] to compose a name, e.g. `funName = "to" + CopyTargetSimpleName`,
 *   or pass a plain literal for a fixed name. When this annotation generates more than one
 *   function (multiple `targets` or a sealed target), use a token so each gets a distinct
 *   name. See `CopyFunctionNameToken.kt`.
 *
 * @see CopyFrom
 * @see CopyVisibility
 * @see DefaultCopyFunctionName
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
annotation class CopyTo(
    vararg val targets: KClass<*>,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
    val funName: String = DefaultCopyFunctionName,
) {
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(
        vararg val propertyNames: String,
    )
}
