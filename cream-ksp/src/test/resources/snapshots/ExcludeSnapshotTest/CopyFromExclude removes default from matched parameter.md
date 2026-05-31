## Generated

````kt
// file: CopyFrom__State.Success.kt
package snap.exclude

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [State.Success])
 * 
 * State -> State.Success copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateSuccess(count = count)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateSuccess(count = count, property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Success
 */
public fun  snap.exclude.State.copyToStateSuccess(
    name: String = this.name,
    count: Int,
) : snap.exclude.State.Success = snap.exclude.State.Success(
    name = name,
    count = count,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyFrom

sealed interface State {
    val name: String
    val count: Int

    @CopyFrom(State::class)
    data class Success(
        val name: String,
        @CopyFrom.Exclude val count: Int,
    )
}
```
