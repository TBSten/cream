## Generated

````kt
// file: CopyTo__Source.kt
package snap.kind.classToSealed

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> State.Loaded copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToStateLoaded(payload = payload)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToStateLoaded(payload = payload, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see State.Loaded
 */
public fun  snap.kind.classToSealed.Source.copyToStateLoaded(
    id: String = this.id,
    payload: Int,
) : snap.kind.classToSealed.State.Loaded = snap.kind.classToSealed.State.Loaded(
    id = id,
    payload = payload,
)

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> State.Loading copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToStateLoading()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToStateLoading(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see State.Loading
 */
public fun  snap.kind.classToSealed.Source.copyToStateLoading(
    id: String = this.id,
) : snap.kind.classToSealed.State.Loading = snap.kind.classToSealed.State.Loading(
    id = id,
)

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
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
public fun  snap.kind.classToSealed.State.copyToStateLoaded(
    id: String = this.id,
    payload: Int,
) : snap.kind.classToSealed.State.Loaded = snap.kind.classToSealed.State.Loaded(
    id = id,
    payload = payload,
)

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
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
public fun  snap.kind.classToSealed.State.copyToStateLoading(
    id: String = this.id,
) : snap.kind.classToSealed.State.Loading = snap.kind.classToSealed.State.Loading(
    id = id,
)
````

## Input

```kt
package snap.kind.classToSealed

import me.tbsten.cream.CopyTo

@CopyTo(State::class)
data class Source(val id: String)

sealed interface State {
    val id: String

    data class Loading(override val id: String) : State
    data class Loaded(override val id: String, val payload: Int) : State
}
```
