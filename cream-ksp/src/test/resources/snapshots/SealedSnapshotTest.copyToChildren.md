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
