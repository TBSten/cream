## Generated

````kt
// file: CopyToChildren__State.kt
package snap.kdoc

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [State])
 * 
 * State -> State.Initial copy function.
 * 
 * Shared note for every State subclass.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateInitial()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateInitial(property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Initial
 */
public fun  snap.kdoc.State.copyToStateInitial(
    id: String = this.id,
) : snap.kdoc.State.Initial = snap.kdoc.State.Initial(
    id = id,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [State])
 * 
 * State -> State.Loaded copy function.
 * 
 * Shared note for every State subclass.
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
public fun  snap.kdoc.State.copyToStateLoaded(
    id: String = this.id,
    payload: Int,
) : snap.kdoc.State.Loaded = snap.kdoc.State.Loaded(
    id = id,
    payload = payload,
)
````

## Input

```kt
package snap.kdoc

import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.KDoc

@CopyToChildren(
    kdoc = KDoc(
        description = "Shared note for every State subclass.",
    ),
)
sealed interface State {
    val id: String

    data class Initial(override val id: String) : State
    data class Loaded(override val id: String, val payload: Int) : State
}
```
