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
 * # Exclude
 *
 * Annotate a **source-class constructor parameter** (or property) with [CopyTo.Exclude] to drop
 * its `= this.<property>` auto-copy default. The matching parameter stays in the generated copy
 * function's signature but becomes required at the call site. See [CopyTo.Exclude] for details
 * and an example.
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
 * @see CopyTo.Exclude
 * @see CopyVisibility
 * @see DefaultCopyFunctionName
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
public annotation class CopyTo(
    vararg val targets: KClass<*>,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
    val funName: String = DefaultCopyFunctionName,
) {
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
    public annotation class Map(
        vararg val propertyNames: String,
    )

    /**
     * Remove the auto-copy default from a matched constructor parameter, making it required.
     *
     * Place this annotation on a **source-class constructor parameter** (or property) that should
     * not be copied automatically. The corresponding parameter in the generated copy function keeps
     * its position in the signature but loses the `= this.<property>` default, forcing the caller
     * to provide an explicit value.
     *
     * Applying `@CopyTo.Exclude` to a parameter that is not matched to any target parameter has
     * no effect and emits a KSP warning.
     *
     * # Example
     *
     * ```kt
     * @CopyTo(Target::class)
     * data class Source(
     *     val name: String,
     *     @CopyTo.Exclude val count: Int,  // caller must specify count explicitly
     * )
     *
     * data class Target(val name: String, val count: Int)
     *
     * // Generated:
     * fun Source.copyToTarget(
     *     name: String = this.name,
     *     count: Int,                       // no default — required
     * ): Target = Target(name = name, count = count)
     * ```
     *
     * @see CopyTo.Map
     * @see CopyTo
     */
    @Target(AnnotationTarget.VALUE_PARAMETER)
    public annotation class Exclude
}
