package me.tbsten.cream

/**
 * Generate one `copy()` extension on a sealed type. It updates the abstract properties
 * declared on the sealed parent and keeps the concrete subtype: `when (this)` dispatches
 * to each subtype's own `copy(...)`, so a `Loading` stays a `Loading`.
 *
 * # Example
 *
 * ```kt
 * @SealedCopy
 * sealed interface MyState {
 *   val name: String
 *   val count: Int
 *   data class Loading(override val name: String, override val count: Int) : MyState
 *   data class Success(
 *       override val name: String,
 *       override val count: Int,
 *       val data: String,
 *   ) : MyState
 * }
 *
 * // Auto generate
 *
 * fun MyState.copy(
 *   name: String = this.name,
 *   count: Int = this.count,
 * ): MyState = when (this) {
 *   is MyState.Loading -> this.copy(name = name, count = count)
 *   is MyState.Success -> this.copy(name = name, count = count)
 * }
 * ```
 *
 * # Difference from [CopyToChildren]
 *
 * - `@CopyToChildren` — the caller picks which subtype to **produce**: one function per child,
 *   returning that child (`MyState.copyToMyStateLoading(...): MyState.Loading`). Use it for
 *   state transitions like `Loading → Success`.
 * - `@SealedCopy` — the caller does not need to know which subtype it **holds**: one function,
 *   keeping the subtype (`MyState.copy(...): MyState`). Use it to update shared properties.
 *
 * Both annotations may coexist on the same sealed type.
 *
 * # Difference from Arrow Optics
 *
 * Arrow Optics's `@optics` generates composable `Lens` / `Prism` values — powerful when you
 * chain many independent updates, but it adds the Arrow runtime and optics-style call sites.
 * `@SealedCopy` emits a plain Kotlin extension that reads like an ordinary `copy(...)` — no
 * extra DSL, no extra dependency.
 *
 * # NonCopyableStrategy
 *
 * Not every subtype has a `copy(...)` to delegate to:
 *
 * - `object` / `data object` subtypes are singletons — there is nothing to copy.
 * - A normal `class` may lack a compatible `copy(...)` member (and a [SealedCopy.Via]
 *   redirect to one).
 *
 * [nonCopyableStrategy] chooses what the generated `copy()` does with such branches:
 *
 * - [NonCopyableStrategy.INHERIT] (default) — defer to the project-level
 *   `cream.nonCopyableStrategy` KSP option; when that is unset too, [NonCopyableStrategy.ERROR]
 *   applies.
 * - [NonCopyableStrategy.ERROR] — refuse to generate; the build fails at KSP time
 *   with a message naming the offending subtype(s). This is the effective default.
 * - [NonCopyableStrategy.RETURN_AS_IS] — emit `is X -> this`; the original instance is
 *   returned unchanged and the parameter updates do not apply to that subtype.
 * - [NonCopyableStrategy.RETURN_NULL] — widen the return type to nullable and emit
 *   `is X -> null`; callers must handle the nullable result.
 *
 * See each [NonCopyableStrategy] entry's KDoc for the concrete generated code.
 *
 * # Multiple annotations
 *
 * `@SealedCopy` is `@Repeatable`. Stack multiple annotations to generate variants
 * with different [funName] and/or [nonCopyableStrategy]:
 *
 * ```kt
 * @SealedCopy(funName = "withUpdated", nonCopyableStrategy = NonCopyableStrategy.ERROR)
 * @SealedCopy(funName = "withUpdatedOrNull", nonCopyableStrategy = NonCopyableStrategy.RETURN_NULL)
 * sealed interface MyState { /* ... */ }
 * ```
 *
 * # Exclude
 *
 * [SealedCopy.Exclude] on an abstract property removes its `= this.<property>` default, so the
 * caller must pass that parameter explicitly. See [SealedCopy.Exclude] for an example.
 *
 * @property funName Template for the generated extension function name. Defaults to
 *   [DefaultCopyFunctionName], which for `@SealedCopy` resolves to `"copy"`. Override with a
 *   plain name (e.g. `"withUpdated"`) when an extension with the same name already exists or
 *   when you stack multiple `@SealedCopy` annotations, or embed a token such as
 *   [CopyTargetSimpleName] (which renders the sealed type's own name). See
 *   `CopyFunctionNameToken.kt`.
 * @property nonCopyableStrategy How to handle subtypes that have no `copy(...)` to delegate to
 *   (objects, or normal classes without a compatible copy function). Defaults to
 *   [NonCopyableStrategy.INHERIT], which defers to the project-level `cream.nonCopyableStrategy`
 *   option; when that is unset too, the effective strategy is [NonCopyableStrategy.ERROR].
 * @property kdoc Custom KDoc (description / examples) injected into the generated
 *   function's KDoc, the same way as cream's other source annotations. See [KDoc].
 * @property visibility Visibility modifier of the generated extension function. Defaults to
 *   [CopyVisibility.INHERIT], which keeps cream's existing behaviour (the function inherits
 *   the sealed type's visibility).
 *
 * @see CopyToChildren
 * @see SealedCopy.Exclude
 * @see NonCopyableStrategy
 * @see CopyVisibility
 * @see DefaultCopyFunctionName
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
public annotation class SealedCopy(
    val funName: String = DefaultCopyFunctionName,
    val nonCopyableStrategy: NonCopyableStrategy = NonCopyableStrategy.INHERIT,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
) {
    /**
     * Select the function a `@SealedCopy` branch delegates to.
     *
     * By default each branch calls the subtype's own `copy(...)` — the synthetic one of a
     * `data class`, or a hand-written member that accepts every abstract property. Annotate a
     * function with `@SealedCopy.Via` when there is no such `copy(...)`, or when the delegate
     * has a different name or parameter shape; the generated branch then calls it instead.
     *
     * The delegate's parameters do not have to mirror the abstract property names:
     * - a parameter named after an abstract property receives that property,
     * - [SealedCopy.Map] binds a differently-named parameter to a property,
     * - any other parameter just needs a default value.
     *
     * cream validates the delegate at compile time: every abstract property must be supplied,
     * and every parameter must be bound or defaulted. A gap is a compile-time error — never a
     * silently mis-generated function.
     *
     * # Example
     *
     * ```kt
     * @SealedCopy
     * sealed interface MyState {
     *   val name: String
     *   val count: Int
     *
     *   class Custom(
     *       override val name: String,
     *       override val count: Int,
     *   ) : MyState {
     *     @SealedCopy.Via
     *     fun cloneWith(
     *         name: String,                          // matched to abstract property `name`
     *         @SealedCopy.Map("count") amount: Int,  // matched to abstract property `count`
     *     ): Custom = Custom(name = name, count = amount)
     *   }
     * }
     *
     * // The Custom branch of the generated MyState.copy(...) becomes:
     * //   is MyState.Custom -> this.cloneWith(name = name, amount = count)
     * ```
     *
     * @see SealedCopy.Map
     * @see SealedCopy
     */
    @Target(AnnotationTarget.FUNCTION)
    public annotation class Via

    /**
     * Bind a [SealedCopy.Via] function's value parameter to an abstract property of the sealed
     * parent by name (**name mapping**), when the parameter name differs from the property name.
     *
     * Place this on a parameter of a `@SealedCopy.Via`-annotated function. Without it, a parameter
     * is matched to the abstract property that shares its name; with it, the parameter receives the
     * abstract property named [value] instead.
     *
     * This mirrors `@CopyTo.Map` / `@CopyFrom.Map` and the other annotations' `.Map`.
     *
     * # Example
     *
     * ```kt
     * @SealedCopy.Via
     * fun cloneWith(
     *     name: String,                          // matched to abstract property `name`
     *     @SealedCopy.Map("count") amount: Int,  // matched to abstract property `count`
     * ): Custom = Custom(name = name, count = amount)
     * ```
     *
     * @property value The name of the abstract property (declared on the sealed parent) this
     *   parameter should receive.
     * @see SealedCopy.Via
     */
    @Target(AnnotationTarget.VALUE_PARAMETER)
    public annotation class Map(
        val value: String,
    )

    /**
     * Remove the auto-copy default from a sealed parent's abstract property, making the
     * corresponding parameter required in the generated `copy()` function.
     *
     * Place this annotation on an **abstract property declared on the sealed parent**. The
     * corresponding parameter in the generated `copy(...)` keeps its position but loses the
     * `= this.<property>` default, forcing the caller to provide an explicit value.
     *
     * This annotation only affects the `@SealedCopy`-generated `copy()` and does **not**
     * affect any `@CopyToChildren`-generated functions on the same sealed type.
     *
     * Applying `@SealedCopy.Exclude` to a property that does not appear in the generated
     * function's parameter list has no effect and emits a KSP warning.
     *
     * # Example
     *
     * ```kt
     * @SealedCopy
     * sealed interface MyState {
     *   val name: String
     *   @SealedCopy.Exclude val count: Int  // caller must specify count explicitly
     *   data class Loading(override val name: String, override val count: Int) : MyState
     * }
     *
     * // Generated:
     * fun MyState.copy(
     *   name: String = this.name,
     *   count: Int,                          // no default — required
     * ): MyState = when (this) { ... }
     * ```
     *
     * @see SealedCopy.Via
     * @see SealedCopy
     * @see CopyToChildren.Exclude
     */
    @Target(AnnotationTarget.PROPERTY)
    public annotation class Exclude
}

/**
 * Strategy for handling subtypes that cannot delegate to a `copy(...)` function inside
 * `@SealedCopy`-generated code.
 *
 * A subtype is considered "non-copyable" when:
 * - It is an `object` / `data object` (singleton, no copy concept), OR
 * - It is a normal `class` that has no compatible `copy(...)` member function
 *   (and no [SealedCopy.Via] redirect to one).
 *
 * [ERROR], [RETURN_AS_IS] and [RETURN_NULL] are the three real strategies; they differ in **what
 * cream emits for the non-copyable branches** and, for [RETURN_NULL], in the return type of the
 * generated function. [INHERIT] is a sentinel that defers to the project-level
 * `cream.nonCopyableStrategy` option. Given:
 *
 * ```kt
 * @SealedCopy(nonCopyableStrategy = <strategy>)
 * sealed interface MyState {
 *   val name: String
 *   data class Loading(override val name: String) : MyState
 *   data object Empty : MyState { override val name = "" }
 *   class Frozen(override val name: String) : MyState
 * }
 * ```
 *
 * see each entry's KDoc for the concrete generated code.
 *
 * @see SealedCopy.nonCopyableStrategy
 */
public enum class NonCopyableStrategy {
    /**
     * Sentinel: defer to the project-level `cream.nonCopyableStrategy` KSP option. This is the
     * default for [SealedCopy.nonCopyableStrategy], mirroring [CopyVisibility.INHERIT] for `visibility`.
     *
     * Resolution order:
     * 1. An explicit annotation `nonCopyableStrategy` (anything other than `INHERIT`) — always wins.
     * 2. Otherwise the `cream.nonCopyableStrategy` option, when it selects a real strategy.
     * 3. Otherwise [ERROR] — the effective default when nothing is configured (fully backward compatible).
     *
     * `INHERIT` is never itself a real strategy: it is resolved to one of the three before generation.
     */
    INHERIT,

    /**
     * Refuse to generate the function. The KSP processor raises an
     * `InvalidCreamUsageException` whose message names the offending subtype(s) and
     * recommends the other strategy values (or `@SealedCopy.Via`).
     *
     * This is the **effective default** when neither the annotation nor `cream.nonCopyableStrategy`
     * selects a strategy, because silent fallbacks for non-data classes are usually a design mistake
     * the author should see early.
     *
     * # Generated code
     *
     * Nothing — the build fails at KSP time with a message like:
     *
     * ```text
     * Cannot generate copy() for sealed type 'MyState' because it contains object
     * subtype(s): MyState.Empty. Objects are singletons and have no .copy() to
     * delegate to.
     *
     * Choose one of the following strategies on @SealedCopy:
     *   • @SealedCopy(nonCopyableStrategy = RETURN_AS_IS)
     *   • @SealedCopy(nonCopyableStrategy = RETURN_NULL)
     *   • Make the subtype a 'data class', add a 'copy(...)' member, or
     *     annotate its copy-shaped function with @SealedCopy.Via
     * ```
     */
    ERROR,

    /**
     * Emit `is X -> this` for each non-copyable branch. The original instance is
     * returned unchanged; the parameter updates do not apply to that subtype.
     *
     * Pick this when "no-op for these branches" is the intended semantics — typically
     * because the non-copyable subtype has no per-instance state worth updating
     * (e.g. a `data object` representing an "empty" state).
     *
     * # Generated code
     *
     * ```kt
     * public fun MyState.copy(
     *   name: String = this.name,
     * ): MyState = when (this) {
     *   is MyState.Loading -> this.copy(name = name)
     *   MyState.Empty -> this
     *   is MyState.Frozen -> this
     * }
     * ```
     */
    RETURN_AS_IS,

    /**
     * Widen the return type to nullable and emit `is X -> null` for each non-copyable
     * branch. Callers must handle the nullable result.
     *
     * Pick this when downstream code should treat the non-copyable branches as a
     * negative outcome (e.g. "I tried to update, but this state can't be updated;
     * decide what to do") rather than silently no-op'ing.
     *
     * # Generated code
     *
     * ```kt
     * public fun MyState.copy(
     *   name: String = this.name,
     * ): MyState? = when (this) {
     *   is MyState.Loading -> this.copy(name = name)
     *   MyState.Empty -> null
     *   is MyState.Frozen -> null
     * }
     * ```
     */
    RETURN_NULL,
}
