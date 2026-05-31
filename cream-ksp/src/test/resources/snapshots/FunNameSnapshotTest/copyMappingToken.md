## Generated

````kt
// file: CopyMapping__Mapping.kt
package snap.funname

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * First -> Second copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = First(...)
 * val target = source.toSecond(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = First(...)
 * val target = source.toSecond(extra = extra, property = value)
 * ```
 * 
 * 
 * @see First
 * @see Second
 */
public fun  snap.funname.First.toSecond(
    shared: String = this.shared,
    extra: Int,
) : snap.funname.Second = snap.funname.Second(
    shared = shared,
    extra = extra,
)
````

## Input

```kt
package snap.funname

import me.tbsten.cream.CopyMapping
import me.tbsten.cream.CopyTargetSimpleName

data class First(val shared: String)
data class Second(val shared: String, val extra: Int)

@CopyMapping(First::class, Second::class, funName = "to" + CopyTargetSimpleName)
object Mapping
```
