## Generated

````kt
// file: CopyTo__DpS.kt
package snap.deprecated.sourceclass

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
@Deprecated("source is deprecated")
public fun  snap.deprecated.sourceclass.DpS.copyToDpT(
    old: Int = this.old,
) : snap.deprecated.sourceclass.DpT = snap.deprecated.sourceclass.DpT(
    old = old,
)
````

## Input

```kt
package snap.deprecated.sourceclass

import me.tbsten.cream.CopyTo

@Deprecated("source is deprecated")
@CopyTo(DpT::class)
data class DpS(val old: Int)

data class DpT(val old: Int)
```
