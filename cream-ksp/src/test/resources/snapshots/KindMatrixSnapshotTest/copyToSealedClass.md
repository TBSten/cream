## Generated

````kt
// file: CopyTo__Source.kt
package snap.kind.classToSealedClass

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
 * val target = source.copyToStateLoaded(loadedId = loadedId, payload = payload)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToStateLoaded(loadedId = loadedId, payload = payload, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see State.Loaded
 */
public fun  snap.kind.classToSealedClass.Source.copyToStateLoaded(
    loadedId: String,
    payload: Int,
) : snap.kind.classToSealedClass.State.Loaded = snap.kind.classToSealedClass.State.Loaded(
    loadedId = loadedId,
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
 * val target = source.copyToStateLoading(loadingId = loadingId)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToStateLoading(loadingId = loadingId, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see State.Loading
 */
public fun  snap.kind.classToSealedClass.Source.copyToStateLoading(
    loadingId: String,
) : snap.kind.classToSealedClass.State.Loading = snap.kind.classToSealedClass.State.Loading(
    loadingId = loadingId,
)
````

## Input

```kt
package snap.kind.classToSealedClass

import me.tbsten.cream.CopyTo

@CopyTo(State::class)
data class Source(val id: String)

sealed class State(val id: String) {
    data class Loading(val loadingId: String) : State(loadingId)
    data class Loaded(val loadedId: String, val payload: Int) : State(loadedId)
}
```
