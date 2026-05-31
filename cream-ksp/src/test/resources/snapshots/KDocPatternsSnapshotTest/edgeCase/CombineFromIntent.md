## Generated

````kt
// file: CombineFrom__LoadingState__SuccessState.kt
package snap.kdoc.combinefromintent

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [SuccessState])
 * 
 * [LoadingState] + [SuccessAction] -> [SuccessState] copy function.
 * 
 * 進行中の状態とアクション結果を合成して成功状態を組み立てる。lastUpdateAt は呼び出し側で指定する。
 * 
 * # Example: Basic
 * 
 * ```kt
 * val loadingState = LoadingState(...)
 * val successAction = SuccessAction(...)
 * val target = loadingState.copyToSuccessState(successAction = SuccessAction(...), lastUpdateAt = lastUpdateAt)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val loadingState = LoadingState(...)
 * val successAction = SuccessAction(...)
 * val target = loadingState.copyToSuccessState(successAction = SuccessAction(...), lastUpdateAt = lastUpdateAt, property = value)
 * ```
 * 
 * 
 * @see LoadingState
 * @see SuccessAction
 * @see SuccessState
 */
public fun  snap.kdoc.combinefromintent.LoadingState.copyToSuccessState(
    successAction: snap.kdoc.combinefromintent.SuccessAction,
    itemId: String = this.itemId,
    data: String = successAction.data,
    lastUpdateAt: String,
) : snap.kdoc.combinefromintent.SuccessState = snap.kdoc.combinefromintent.SuccessState(
    itemId = itemId,
    data = data,
    lastUpdateAt = lastUpdateAt,
)
````

## Input

```kt
package snap.kdoc.combinefromintent

import me.tbsten.cream.CombineFrom
import me.tbsten.cream.KDoc

data class LoadingState(val itemId: String)

data class SuccessAction(val data: String)

@CombineFrom(
    LoadingState::class,
    SuccessAction::class,
    kdoc = KDoc(description = "進行中の状態とアクション結果を合成して成功状態を組み立てる。lastUpdateAt は呼び出し側で指定する。"),
)
data class SuccessState(val itemId: String, val data: String, val lastUpdateAt: String)
```
