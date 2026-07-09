package me.tbsten.cream

import kotlin.reflect.KClass

/**
 * Generate bridge functions that call the annotated function from the [sources]
 * argument-holder classes.
 *
 * Each source class produces one bridge. By default the bridge keeps the **same name as the
 * annotated function** (an overload); set [funName] to give it a different name instead. The
 * bridge takes the source object as its first parameter and defaults each matching parameter to
 * the corresponding source property, so callers can pass the holder object alone or override
 * individual parameters.
 *
 * # Example
 *
 * ```kt
 * data class ProcessDataArgs(val data1: String, val data2: Int)
 *
 * @CallFrom(ProcessDataArgs::class)
 * fun processData(data1: String, data2: Int) { /* ... */ }
 *
 * // Auto generate
 *
 * public fun processData(
 *     processDataArgs: ProcessDataArgs,
 *     data1: String = processDataArgs.data1,
 *     data2: Int = processDataArgs.data2,
 * ): Unit = processData(
 *     data1 = data1,
 *     data2 = data2,
 * )
 * ```
 *
 * A member function is bridged as an extension function on its enclosing class
 * (`fun Enclosing.processData(...)`), because KSP cannot add members to an existing class.
 * An extension function is bridged as an extension function on the same receiver.
 *
 * # Parameters that do not match a source property
 *
 * - With a default value on the annotated function: the parameter is **omitted** from the
 *   bridge (and from the delegating call), so the function's own default still applies.
 *   To override it, call the original function directly.
 * - Without a default value: the parameter stays in the bridge as a required parameter.
 *
 * # Exclude
 *
 * Annotate a **parameter of the annotated function** with [CallFrom.Exclude] to drop its
 * auto-copied default. The parameter stays in the generated overload's signature but becomes
 * required at the call site. See [CallFrom.Exclude] for details.
 *
 * @property visibility Visibility modifier of the generated bridge function. Defaults to
 *   [CopyVisibility.INHERIT], which makes the function inherit the annotated function's
 *   visibility — lowered to `internal` when the function, its enclosing class, or a source
 *   class is `internal` (a more visible bridge could not compile).
 * @property funName Name of the generated bridge function. Defaults to
 *   [DefaultCallFromFunctionName], which keeps the annotated function's own name (the overload
 *   behaviour). A custom value is used as a **plain literal** — `@CallFrom` does not support the
 *   `CopyTarget*` naming tokens, because it has no target class to render; the name is emitted
 *   verbatim (escaped for keyword collisions). When two bridges would end up with the same
 *   `funName` **and** the same parameter types — e.g. a custom name shared by two functions, or a
 *   custom name that already exists as a hand-written function — cream reports a positioned
 *   compilation error instead of emitting conflicting overloads. Bridges from different source
 *   classes never collide on their own: each has a distinct first parameter type.
 *
 * @see CopyFrom
 * @see CallFrom.Map
 * @see CallFrom.Exclude
 * @see CopyVisibility
 * @see DefaultCallFromFunctionName
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
public annotation class CallFrom(
    vararg val sources: KClass<*>,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
    val funName: String = DefaultCallFromFunctionName,
) {
    /**
     * Map a parameter of the annotated function to differently named source properties.
     *
     * Place this annotation on a **parameter of the annotated function** and list the source
     * property names that should supply its default value, when the names do not match.
     *
     * # Example
     *
     * ```kt
     * data class ProcessDataArgs(val rawData: String)
     *
     * @CallFrom(ProcessDataArgs::class)
     * fun processData(@CallFrom.Map("rawData") data1: String) { /* ... */ }
     *
     * // Generated:
     * public fun processData(
     *     processDataArgs: ProcessDataArgs,
     *     data1: String = processDataArgs.rawData,
     * ): Unit = processData(data1 = data1)
     * ```
     *
     * @see CallFrom
     */
    @Target(AnnotationTarget.VALUE_PARAMETER)
    public annotation class Map(
        vararg val propertyNames: String,
    )

    /**
     * Remove the auto-copied default from a matched parameter, making it required.
     *
     * Place this annotation on a **parameter of the annotated function** that should not
     * receive the auto-copied value from the source. The parameter keeps its position in the
     * generated overload's signature but loses the `= <source>.<property>` default, forcing
     * the caller to provide an explicit value.
     *
     * Applying `@CallFrom.Exclude` to a parameter that is not matched to any source property
     * has no effect and emits a KSP warning.
     *
     * # Example
     *
     * ```kt
     * data class ProcessDataArgs(val data1: String, val data2: Int)
     *
     * @CallFrom(ProcessDataArgs::class)
     * fun processData(
     *     data1: String,
     *     @CallFrom.Exclude data2: Int, // caller must specify data2 explicitly
     * ) { /* ... */ }
     *
     * // Generated:
     * public fun processData(
     *     processDataArgs: ProcessDataArgs,
     *     data1: String = processDataArgs.data1,
     *     data2: Int, // no default — required
     * ): Unit = processData(data1 = data1, data2 = data2)
     * ```
     *
     * @see CallFrom.Map
     * @see CallFrom
     */
    @Target(AnnotationTarget.VALUE_PARAMETER)
    public annotation class Exclude
}
