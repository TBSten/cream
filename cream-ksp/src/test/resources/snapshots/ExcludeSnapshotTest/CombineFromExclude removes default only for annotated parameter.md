## Generated

````kt
// file: CombineFrom__LoadingState__SuccessState.kt
package snap.exclude

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [SuccessState])
 * 
 * [LoadingState] + [SuccessAction] -> [SuccessState] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val loadingState = LoadingState(...)
 * val successAction = SuccessAction(...)
 * val target = loadingState.copyToSuccessState(successAction = SuccessAction(...), data = data, extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val loadingState = LoadingState(...)
 * val successAction = SuccessAction(...)
 * val target = loadingState.copyToSuccessState(successAction = SuccessAction(...), data = data, extra = extra, property = value)
 * ```
 * 
 * 
 * @see LoadingState
 * @see SuccessAction
 * @see SuccessState
 */
public fun  snap.exclude.LoadingState.copyToSuccessState(
    successAction: snap.exclude.SuccessAction,
    itemId: String = this.itemId,
    data: String,
    extra: Int,
) : snap.exclude.SuccessState = snap.exclude.SuccessState(
    itemId = itemId,
    data = data,
    extra = extra,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CombineFrom

data class LoadingState(val itemId: String)
data class SuccessAction(val data: String)

@CombineFrom(LoadingState::class, SuccessAction::class)
data class SuccessState(
    val itemId: String,
    @CombineFrom.Exclude val data: String,
    val extra: Int,
)
```
