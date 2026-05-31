## Generated

````kt
// file: CombineTo__LoadingState__SuccessState.kt
package snap.exclude

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [LoadingState])
 * 
 * [LoadingState] -> [SuccessState] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val loadingState = LoadingState(...)
 * val target = loadingState.copyToSuccessState(sessionId = sessionId, extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val loadingState = LoadingState(...)
 * val target = loadingState.copyToSuccessState(sessionId = sessionId, extra = extra, property = value)
 * ```
 * 
 * 
 * @see LoadingState
 * @see SuccessState
 */
public fun  snap.exclude.LoadingState.copyToSuccessState(
    itemId: String = this.itemId,
    sessionId: String,
    extra: Int,
) : snap.exclude.SuccessState = snap.exclude.SuccessState(
    itemId = itemId,
    sessionId = sessionId,
    extra = extra,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CombineTo

@CombineTo(SuccessState::class)
data class LoadingState(
    val itemId: String,
    @CombineTo.Exclude val sessionId: String,
)

data class SuccessState(
    val itemId: String,
    val sessionId: String,
    val extra: Int,
)
```
