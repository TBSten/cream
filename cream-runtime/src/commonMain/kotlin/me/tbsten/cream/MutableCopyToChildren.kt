package me.tbsten.cream

/**
 * Generate `<annotated by MutableCopyToChildren class>.mutableCopyTo<children class>()` mutable copy functions.
 *
 * When applied to a sealed class/interface, automatically generates mutable copy functions from that sealed
 * class/interface to all classes that inherit from it.
 *
 * This generates functions that copy properties from the source object to a mutable target object
 * with explicit parameter values. Properties with matching names and types use source values as defaults.
 *
 * # Example
 *
 * ```kt
 * @MutableCopyToChildren
 * sealed interface UiState {
 *     data object Loading : UiState
 *
 *     sealed interface Success : UiState {
 *         val data: Data
 *
 *         data class Done(
 *             override val data: Data,
 *         ) : Success
 *
 *         data class Refreshing(
 *             override val data: Data,
 *         ) : Success
 *     }
 * }
 *
 * // Auto generate
 *
 * fun UiState.mutableCopyToUiStateLoading(
 *     mutableTarget: UiState.Loading,
 * ): UiState.Loading = mutableTarget
 *
 * fun UiState.mutableCopyToUiStateSuccessDone(
 *     mutableTarget: UiState.Success.Done,
 *     data: Data = this.data,
 * ): UiState.Success.Done {
 *     mutableTarget.data = data
 *     return mutableTarget
 * }
 *
 * fun UiState.mutableCopyToUiStateSuccessRefreshing(
 *     mutableTarget: UiState.Success.Refreshing,
 *     data: Data = this.data,
 * ): UiState.Success.Refreshing {
 *     mutableTarget.data = data
 *     return mutableTarget
 * }
 * ```
 *
 * # Usage
 *
 * ```kt
 * val uiState: UiState = /* ... */
 * val target = UiState.Success.Done(Data("old"))
 *
 * val result = uiState.mutableCopyToUiStateSuccessDone(
 *     mutableTarget = target,
 *     data = Data("new")
 * )
 * // result.data == Data("new")
 * // result === target (same instance)
 * ```
 *
 * @see CopyToChildren
 * @see MutableCopyTo
 */
@Target(AnnotationTarget.CLASS)
annotation class MutableCopyToChildren(
    val notCopyToObject: Boolean = false,
)
