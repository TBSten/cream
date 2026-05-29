## Generated

````kt
// file: CopyToChildren__UiState.kt
package snap.kdoc.copytochildrenshared

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [UiState])
 * 
 * UiState -> UiState.Loading copy function.
 * 
 * UiState 内の任意の状態へ遷移するための生成関数。遷移元の共通プロパティは引き継がれる。
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = UiState(...)
 * val target = source.copyToUiStateLoading()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = UiState(...)
 * val target = source.copyToUiStateLoading(property = value)
 * ```
 * 
 * 
 * @see UiState
 * @see UiState.Loading
 */
public fun  snap.kdoc.copytochildrenshared.UiState.copyToUiStateLoading(
    sessionId: String = this.sessionId,
) : snap.kdoc.copytochildrenshared.UiState.Loading = snap.kdoc.copytochildrenshared.UiState.Loading(
    sessionId = sessionId,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [UiState])
 * 
 * UiState -> UiState.Success copy function.
 * 
 * UiState 内の任意の状態へ遷移するための生成関数。遷移元の共通プロパティは引き継がれる。
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = UiState(...)
 * val target = source.copyToUiStateSuccess()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = UiState(...)
 * val target = source.copyToUiStateSuccess(property = value)
 * ```
 * 
 * 
 * @see UiState
 * @see UiState.Success
 */
public fun  snap.kdoc.copytochildrenshared.UiState.copyToUiStateSuccess(
    sessionId: String = this.sessionId,
    data: String,
) : snap.kdoc.copytochildrenshared.UiState.Success = snap.kdoc.copytochildrenshared.UiState.Success(
    sessionId = sessionId,
    data = data,
)
````

## Input

```kt
package snap.kdoc.copytochildrenshared

import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.KDoc

@CopyToChildren(
    kdoc = KDoc(description = "UiState 内の任意の状態へ遷移するための生成関数。遷移元の共通プロパティは引き継がれる。"),
)
sealed interface UiState {
    val sessionId: String

    data class Loading(override val sessionId: String) : UiState
    data class Success(override val sessionId: String, val data: String) : UiState
}
```
