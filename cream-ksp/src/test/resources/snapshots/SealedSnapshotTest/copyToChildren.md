## Generated

````kt
// file: CopyToChildren__State.kt
package snap.sealed

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
 * val target = source.copyToStateLoaded(payload = payload)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateLoaded(payload = payload, property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Loaded
 */
public fun  snap.sealed.State.copyToStateLoaded(
    id: String = this.id,
    payload: Int,
) : snap.sealed.State.Loaded = snap.sealed.State.Loaded(
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
public fun  snap.sealed.State.copyToStateLoading(
    id: String = this.id,
) : snap.sealed.State.Loading = snap.sealed.State.Loading(
    id = id,
)
````

## Input

```kt
package snap.sealed

import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface State {
    val id: String

    data class Loading(override val id: String) : State
    data class Loaded(override val id: String, val payload: Int) : State
}
```
