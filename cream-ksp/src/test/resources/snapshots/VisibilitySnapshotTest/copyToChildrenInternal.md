## Generated

````kt
// file: CopyToChildren__State.kt
package snap.visibility

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [State])
 * 
 * State -> State.Loaded copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateLoaded()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateLoaded(property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Loaded
 */
internal fun  snap.visibility.State.copyToStateLoaded(
    id: String = this.id,
    payload: Int,
) : snap.visibility.State.Loaded = snap.visibility.State.Loaded(
    id = id,
    payload = payload,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [State])
 * 
 * State -> State.Loading copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateLoading()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateLoading(property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Loading
 */
internal fun  snap.visibility.State.copyToStateLoading(
    id: String = this.id,
) : snap.visibility.State.Loading = snap.visibility.State.Loading(
    id = id,
)
````

## Input

```kt
package snap.visibility

import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.CopyVisibility

@CopyToChildren(visibility = CopyVisibility.INTERNAL)
sealed interface State {
    val id: String

    data class Loading(override val id: String) : State
    data class Loaded(override val id: String, val payload: Int) : State
}
```
