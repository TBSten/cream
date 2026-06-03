## Generated

````kt
// file: CopyTo__DpS.kt
package snap.deprecated.none

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [DpS])
 * 
 * DpS -> DpT copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = DpS(...)
 * val target = source.copyToDpT()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = DpS(...)
 * val target = source.copyToDpT(property = value)
 * ```
 * 
 * 
 * @see DpS
 * @see DpT
 */
public fun  snap.deprecated.none.DpS.copyToDpT(
    old: Int = this.old,
) : snap.deprecated.none.DpT = snap.deprecated.none.DpT(
    old = old,
)
````

## Input

```kt
package snap.deprecated.none

import me.tbsten.cream.CopyTo

@CopyTo(DpT::class)
data class DpS(val old: Int)

data class DpT(val old: Int)
```
