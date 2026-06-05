package me.tbsten.cream

/**
 * Generate copy functions from a sealed type down to its concrete leaves, each one
 * returning the **specific leaf** (i.e. a type-narrowing transition). For a flat sealed
 * type these are its direct children; for nested hierarchies the generation recurses
 * through any intermediate sealed *types* (not just the direct children).
 *
 * # Example
 *
 * ```kt
 * @CopyToChildren
 * sealed interface UiState {
 *   val sessionId: String
 *   data class Loading(override val sessionId: String) : UiState
 *   data class Success(override val sessionId: String, val data: String) : UiState
 * }
 *
 * // Auto generate
 *
 * fun UiState.copyToUiStateLoading(
 *   sessionId: String = this.sessionId,
 * ): UiState.Loading = /* ... */
 *
 * fun UiState.copyToUiStateSuccess(
 *   sessionId: String = this.sessionId,
 *   data: String,
 * ): UiState.Success = /* ... */
 * ```
 *
 * # Difference from [SealedCopy]
 *
 * `@SealedCopy` generates a single type-preserving extension on the sealed parent
 * (`UiState.copy(...): UiState`) that dispatches to each subtype's own `copy`. Use
 * it when you want to update shared properties without changing the concrete subtype.
 *
 * `@CopyToChildren` generates per-child copy functions whose return type is the
 * child (`UiState.copyToUiStateLoading(...): UiState.Loading`) — a type-narrowing
 * transition. Use it when you are explicitly transitioning between subtypes (e.g.
 * `Loading → Success`).
 *
 * Both annotations may coexist on the same sealed type when both shapes are useful.
 *
 * # Exclude
 *
 * Annotate a **property declared on the sealed parent** with [CopyToChildren.Exclude] to drop its
 * `= this.<property>` auto-copy default. The matching parameter becomes required at the call site
 * across **all** per-child copy functions. This affects only the `@CopyToChildren`-generated
 * functions and not any `@SealedCopy`-generated `copy()`. See [CopyToChildren.Exclude] for details
 * and an example.
 *
 * @property notCopyToObject When `true`, skip generating copy functions whose target
 *   child is an `object` (those would just return the singleton). Defaults to `false`.
 * @property visibility Visibility modifier applied to every generated per-child copy
 *   function. Defaults to [CopyVisibility.INHERIT], which keeps cream's existing behaviour
 *   (each function inherits its target child's visibility).
 *
 * @see SealedCopy
 * @see CopyTo
 * @see CopyToChildren.Exclude
 * @see CopyVisibility
 */
@Target(AnnotationTarget.CLASS)
annotation class CopyToChildren(
    val notCopyToObject: Boolean = false,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
) {
    /**
     * Remove the auto-copy default from a sealed parent's property across **all**
     * `@CopyToChildren`-generated per-child copy functions, making the corresponding
     * parameter required in every generated function.
     *
     * Place this annotation on a **property declared on the sealed parent**. The
     * corresponding parameter in every generated `copyTo<Child>(...)` function loses the
     * `= this.<property>` default, forcing the caller to supply an explicit value regardless
     * of which child type they are transitioning to.
     *
     * This annotation only affects the `@CopyToChildren`-generated per-child functions and
     * does **not** affect any `@SealedCopy`-generated `copy()` on the same sealed type.
     *
     * Applying `@CopyToChildren.Exclude` to a property that does not appear in any
     * generated per-child function's parameter list has no effect and emits a KSP warning.
     *
     * # Example
     *
     * ```kt
     * @CopyToChildren
     * sealed interface UiState {
     *   val sessionId: String
     *   @CopyToChildren.Exclude val count: Int  // caller must specify count explicitly
     *   data class Loading(override val sessionId: String, override val count: Int) : UiState
     *   data class Success(override val sessionId: String, override val count: Int, val data: String) : UiState
     * }
     *
     * // Generated:
     * fun UiState.copyToUiStateLoading(
     *   sessionId: String = this.sessionId,
     *   count: Int,                             // no default — required
     * ): UiState.Loading = /* ... */
     *
     * fun UiState.copyToUiStateSuccess(
     *   sessionId: String = this.sessionId,
     *   count: Int,                             // no default — required
     *   data: String,
     * ): UiState.Success = /* ... */
     * ```
     *
     * @see CopyToChildren
     * @see SealedCopy.Exclude
     */
    @Target(AnnotationTarget.PROPERTY)
    annotation class Exclude
}
