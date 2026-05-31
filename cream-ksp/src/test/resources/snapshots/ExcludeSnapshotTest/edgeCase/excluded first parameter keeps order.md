## Generated

````kt
// file: CopyFrom__Dst.kt
package snap.exclude

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Dst])
 * 
 * Src -> Dst copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Src(...)
 * val target = source.copyToDst(id = id)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Src(...)
 * val target = source.copyToDst(id = id, property = value)
 * ```
 * 
 * 
 * @see Src
 * @see Dst
 */
public fun  snap.exclude.Src.copyToDst(
    id: String,
    name: String = this.name,
    count: Int = this.count,
) : snap.exclude.Dst = snap.exclude.Dst(
    id = id,
    name = name,
    count = count,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyFrom

data class Src(val id: String, val name: String, val count: Int)

@CopyFrom(Src::class)
data class Dst(
    @CopyFrom.Exclude val id: String,
    val name: String,
    val count: Int,
)
```
