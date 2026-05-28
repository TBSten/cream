````kt
// file: CopyToChildren__State.kt
package snap.kind.sealedMixed

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [State])
 * 
 * State -> State.Initial copy function.
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
public fun  snap.kind.sealedMixed.State.copyToStateLoaded(
    id: String = this.id,
    payload: Int,
) : snap.kind.sealedMixed.State.Loaded = snap.kind.sealedMixed.State.Loaded(
    id = id,
    payload = payload,
)
````
