## Generated

````kt
// file: CopyToChildren__UiState.kt
package snap.exclude

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [UiState])
 * 
 * UiState -> UiState.Loading copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = UiState(...)
 * val target = source.copyToUiStateLoading(count = count)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = UiState(...)
 * val target = source.copyToUiStateLoading(count = count, property = value)
 * ```
 * 
 * 
 * @see UiState
 * @see UiState.Loading
 */
public fun  snap.exclude.UiState.copyToUiStateLoading(
    sessionId: String = this.sessionId,
    count: Int,
) : snap.exclude.UiState.Loading = snap.exclude.UiState.Loading(
    sessionId = sessionId,
    count = count,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [UiState])
 * 
 * UiState -> UiState.Success copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = UiState(...)
 * val target = source.copyToUiStateSuccess(count = count, data = data)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = UiState(...)
 * val target = source.copyToUiStateSuccess(count = count, data = data, property = value)
 * ```
 * 
 * 
 * @see UiState
 * @see UiState.Success
 */
public fun  snap.exclude.UiState.copyToUiStateSuccess(
    sessionId: String = this.sessionId,
    count: Int,
    data: String,
) : snap.exclude.UiState.Success = snap.exclude.UiState.Success(
    sessionId = sessionId,
    count = count,
    data = data,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface UiState {
    val sessionId: String
    @CopyToChildren.Exclude val count: Int
    data class Loading(override val sessionId: String, override val count: Int) : UiState
    data class Success(
        override val sessionId: String,
        override val count: Int,
        val data: String,
    ) : UiState
}
```
