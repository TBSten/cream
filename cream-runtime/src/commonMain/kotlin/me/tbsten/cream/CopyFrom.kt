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
 * # Exclude
 *
 * Annotate a **target-class constructor parameter** with [CopyFrom.Exclude] to drop its
 * `= this.<property>` auto-copy default. The matching parameter stays in the generated copy
 * function's signature but becomes required at the call site. See [CopyFrom.Exclude] for details
 * and an example.
 *
 * @property visibility Visibility modifier of the generated copy function. Defaults to
 *   [CopyVisibility.INHERIT], which keeps cream's existing behaviour (the function inherits
 *   the target class's visibility).
 * @property funName Template for the generated function name. Defaults to
 *   [DefaultCopyFunctionName] (cream's derived name). Embed naming tokens such as
 *   [CopyTargetSimpleName] to compose a name. When this annotation lists more than one
 *   `sources` (or the annotated type is sealed) use a token so each generated function
 *   gets a distinct name. See `CopyFunctionNameToken.kt`.
 *
 * @see CopyTo
 * @see CopyFrom.Exclude
 * @see CopyVisibility
 * @see DefaultCopyFunctionName
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
public annotation class CopyFrom(
    vararg val sources: KClass<*>,
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
     * Place this annotation on a **target-class constructor parameter** that should not receive
     * the auto-copied value from the source. The parameter keeps its position in the generated
     * copy function's signature but loses the `= this.<property>` default, forcing the caller
     * to provide an explicit value.
     *
     * Applying `@CopyFrom.Exclude` to a parameter that is not matched to any source property
     * has no effect and emits a KSP warning.
     *
     * # Example
     *
     * ```kt
     * @CopyFrom(State::class)
     * data class Success(
     *     val name: String,
     *     @CopyFrom.Exclude val count: Int,  // caller must specify count explicitly
     * )
     *
     * // Generated:
     * fun State.copyToSuccess(
     *     name: String = this.name,
     *     count: Int,                         // no default — required
     * ): Success = Success(name = name, count = count)
     * ```
     *
     * @see CopyFrom.Map
     * @see CopyFrom
     */
    @Target(AnnotationTarget.VALUE_PARAMETER)
    public annotation class Exclude
}
