## Generated

````kt
// file: CopyTo__DpS.kt
package snap.deprecated.levelerror

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
@Deprecated("gone", level = DeprecationLevel.ERROR)
public fun  snap.deprecated.levelerror.DpS.copyToDpT(
    old: Int = this.old,
) : snap.deprecated.levelerror.DpT = snap.deprecated.levelerror.DpT(
    old = old,
)
````

## Input

```kt
package snap.deprecated.levelerror

import me.tbsten.cream.CopyTo

@Deprecated("gone", level = DeprecationLevel.ERROR)
@CopyTo(DpT::class)
data class DpS(val old: Int)

data class DpT(val old: Int)
```
