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
 * val target = source.copyToDst(count = count)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Src(...)
 * val target = source.copyToDst(count = count, property = value)
 * ```
 * 
 * 
 * @see Src
 * @see Dst
 */
public fun  snap.exclude.Src.copyToDst(
    name: String = this.name,
    count: Int,
) : snap.exclude.Dst = snap.exclude.Dst(
    name = name,
    count = count,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyFrom

data class Src(val name: String, val count: Int)

@CopyFrom(Src::class)
data class Dst(
    val name: String,
    @CopyFrom.Exclude val count: Int = 10,
)
```
