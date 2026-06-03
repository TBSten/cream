## Generated

````kt
// file: CopyToChildren__State.kt
package snap.sealed.vararg

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
 * val target = source.copyToStateLoaded(tags = tags)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateLoaded(tags = tags, property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Loaded
 */
public fun  snap.sealed.vararg.State.copyToStateLoaded(
    id: Int = this.id,
    vararg tags: String,
) : snap.sealed.vararg.State.Loaded = snap.sealed.vararg.State.Loaded(
    id = id,
    tags = tags,
)
````

## Input

```kt
package snap.sealed.vararg

import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface State {
    val id: Int

    class Loaded(override val id: Int, vararg val tags: String) : State
}
```
