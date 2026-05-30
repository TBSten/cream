## Generated

````kt
// file: CopyTo__Source.kt
package snap.funname

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
 * val target = source.toLoaded()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toLoaded(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see State.Loaded
 */
public fun  snap.funname.Source.toLoaded(
    id: String = this.id,
    payload: Int,
) : snap.funname.State.Loaded = snap.funname.State.Loaded(
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
 * val target = source.toLoading()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toLoading(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see State.Loading
 */
public fun  snap.funname.Source.toLoading(
    id: String = this.id,
) : snap.funname.State.Loading = snap.funname.State.Loading(
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
 * val target = source.toLoaded()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.toLoaded(property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Loaded
 */
public fun  snap.funname.State.toLoaded(
    id: String = this.id,
    payload: Int,
) : snap.funname.State.Loaded = snap.funname.State.Loaded(
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
 * val target = source.toLoading()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.toLoading(property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Loading
 */
public fun  snap.funname.State.toLoading(
    id: String = this.id,
) : snap.funname.State.Loading = snap.funname.State.Loading(
    id = id,
)
````

## Input

```kt
package snap.funname

import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyTargetSimpleName

@CopyTo(State::class, funName = "to" + CopyTargetSimpleName)
data class Source(val id: String)

sealed interface State {
    val id: String

    data class Loading(override val id: String) : State
    data class Loaded(override val id: String, val payload: Int) : State
}
```
