package me.tbsten.cream

/**
 * Generate one copy function per direct child of a sealed type, each one returning
 * the **specific child** (i.e. a type-narrowing transition).
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
 * @property notCopyToObject When `true`, skip generating copy functions whose target
 *   child is an `object` (those would just return the singleton). Defaults to `false`.
 * @property visibility Visibility modifier applied to every generated per-child copy
 *   function. Defaults to [CopyVisibility.INHERIT], which keeps cream's existing behaviour
 *   (each function inherits its target child's visibility).
 *
 * @see SealedCopy
 * @see CopyTo
 * @see CopyVisibility
 */
@Target(AnnotationTarget.CLASS)
annotation class CopyToChildren(
    val notCopyToObject: Boolean = false,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
)
