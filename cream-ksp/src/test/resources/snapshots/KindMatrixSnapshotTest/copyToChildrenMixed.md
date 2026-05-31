## Generated

````kt
// file: CopyToChildren__State.kt
package snap.kind.sealedMixed

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [State])
 * 
 * State -> State.Initial copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateInitial()
 * ```
 * 
 * 
 * @see State
 * @see State.Initial
 */
public fun snap.kind.sealedMixed.State.copyToStateInitial() = snap.kind.sealedMixed.State.Initial
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
public fun  snap.kind.sealedMixed.State.copyToStateLoaded(
    id: String = this.id,
    payload: Int,
) : snap.kind.sealedMixed.State.Loaded = snap.kind.sealedMixed.State.Loaded(
    id = id,
    payload = payload,
)
````

## Input

```kt
package snap.kind.sealedMixed

import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface State {
    val id: String

    data object Initial : State {
        override val id: String get() = "initial"
    }

    data class Loaded(override val id: String, val payload: Int) : State
}
```
