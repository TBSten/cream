package me.tbsten.cream

/**
 * Generate a `copy()` function on a sealed type that preserves the original subtype
 * while updating abstract properties declared on the sealed parent. A single extension
 * is emitted on the sealed parent and an exhaustive `when` dispatches to each subtype's
 * own `copy(...)`.
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
 * `@CopyToChildren` generates per-child copy functions whose **return type is the
 * child** (e.g. `MyState.copyToMyStateLoading(...): MyState.Loading`). It models a
 * type-narrowing transition where the caller knows which concrete subtype they want
 * to produce — typically `Loading → Success` or similar.
 *
 * `@SealedCopy` keeps the parent type as both receiver and return type
 * (`MyState.copy(...): MyState`). The concrete subtype is preserved by runtime
 * dispatch — the caller does not need to know which one they're holding. Pick this
 * when you only want to update shared properties.
 *
 * Both annotations may coexist on the same sealed type when both shapes are useful.
 *
 * # Difference from Arrow Optics
 *
 * Arrow Optics's `@optics` generates `Lens` / `Prism` values for sealed hierarchies
 * and lets you compose updates through them (`state.copy(MyState.name, "x")`,
 * `MyState.loading.compose(Loading.count).modify(state) { it + 1 }`). It is
 * powerful when you compose many independent updates, but it pulls in the Arrow
 * runtime and reads as optics calls at the use site.
 *
 * `@SealedCopy` emits a plain Kotlin extension whose call site reads like an
 * ordinary `copy(...)` — no extra DSL, no Arrow runtime. Pick Arrow when you need
 * composable optics across many shapes; pick `@SealedCopy` when you just want one
 * `MyState.copy(...)` that does the right thing for the abstract properties of the
 * parent.
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
 * @property funName Name of the generated extension function. Defaults to `"copy"`.
 *   Override if an extension with the same name already exists on the sealed type,
 *   or when you stack multiple `@SealedCopy` annotations and need distinct names.
 * @property nonCopyableStrategy How to handle subtypes that have no `copy(...)` to delegate to
 *   (objects, or normal classes without a compatible copy function). Defaults to
 *   [NonCopyableStrategy.ERROR].
 * @property kdoc Custom KDoc (description / examples) injected into the generated
 *   function's KDoc, the same way as cream's other source annotations. See [KDoc].
 *
 * @see CopyToChildren
 * @see NonCopyableStrategy
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class SealedCopy(
    val funName: String = "copy",
    val nonCopyableStrategy: NonCopyableStrategy = NonCopyableStrategy.ERROR,
    val kdoc: KDoc = KDoc(),
) {
    /**
     * Mark the function `@SealedCopy` should delegate to for a subtype.
     *
     * By default `@SealedCopy` delegates to each subtype's `copy(...)` (the synthetic one
     * for a `data class`). Apply `@SealedCopy.Map` **directly to a copy-shaped function**
     * when the subtype is not a `data class` and its copy lives under a different name —
     * the delegated name is taken from the annotated function itself, so no argument is
     * needed.
     *
     * # Example
     *
     * ```kt
     * @SealedCopy
     * sealed interface MyState {
     *   val name: String
     *
     *   class Custom(override val name: String) : MyState {
     *     @SealedCopy.Map
     *     fun cloneWith(name: String = this.name): Custom = Custom(name)
     *   }
     * }
     * ```
     *
     * The generated `MyState.copy(...)` will then call `this.cloneWith(...)` for the
     * `Custom` branch instead of `this.copy(...)`.
     */
    @Target(AnnotationTarget.FUNCTION)
    annotation class Map
}

/**
 * Strategy for handling subtypes that cannot delegate to a `copy(...)` function inside
 * `@SealedCopy`-generated code.
 *
 * A subtype is considered "non-copyable" when:
 * - It is an `object` / `data object` (singleton, no copy concept), OR
 * - It is a normal `class` that has no compatible `copy(...)` member function
 *   (and no [SealedCopy.Map] redirect to one).
 *
 * The three strategies differ in **what cream emits for the non-copyable branches**
 * and, for [RETURN_NULL], in the return type of the generated function. Given:
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
enum class NonCopyableStrategy {
    /**
     * Refuse to generate the function. The KSP processor raises an
     * `InvalidCreamUsageException` whose message names the offending subtype(s) and
     * recommends the other strategy values (or `@SealedCopy.Map`).
     *
     * This is the default because silent fallbacks for non-data classes are usually a
     * design mistake the author should see early.
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
     *     annotate its copy-shaped function with @SealedCopy.Map
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
