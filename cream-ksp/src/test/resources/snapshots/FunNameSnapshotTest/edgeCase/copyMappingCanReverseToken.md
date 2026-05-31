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
 * val target = source.toSecond()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = First(...)
 * val target = source.toSecond(property = value)
 * ```
 * 
 * 
 * @see First
 * @see Second
 */
public fun  snap.funname.First.toSecond(
    shared: String = this.shared,
) : snap.funname.Second = snap.funname.Second(
    shared = shared,
)

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * Second -> First copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Second(...)
 * val target = source.toFirst()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Second(...)
 * val target = source.toFirst(property = value)
 * ```
 * 
 * 
 * @see Second
 * @see First
 */
public fun  snap.funname.Second.toFirst(
    shared: String = this.shared,
) : snap.funname.First = snap.funname.First(
    shared = shared,
)
````

## Input

```kt
package snap.funname

import me.tbsten.cream.CopyMapping
import me.tbsten.cream.CopyTargetSimpleName

data class First(val shared: String)
data class Second(val shared: String)

@CopyMapping(First::class, Second::class, canReverse = true, funName = "to" + CopyTargetSimpleName)
object Mapping
```
