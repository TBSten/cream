## Generated

````kt
// file: CopyTo__DpS.kt
package snap.deprecated.sourceproperty

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
@Deprecated("prop is deprecated")
public fun  snap.deprecated.sourceproperty.DpS.copyToDpT(
    old: Int = this.old,
) : snap.deprecated.sourceproperty.DpT = snap.deprecated.sourceproperty.DpT(
    old = old,
)
````

## Input

```kt
package snap.deprecated.sourceproperty

import me.tbsten.cream.CopyTo

@CopyTo(DpT::class)
data class DpS(@Deprecated("prop is deprecated") val old: Int)

data class DpT(val old: Int)
```
