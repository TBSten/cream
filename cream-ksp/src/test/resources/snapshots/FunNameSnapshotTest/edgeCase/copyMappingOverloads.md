## Generated

````kt
// file: CopyMapping__Mapping.kt
package snap.funname

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * A -> B copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = A(...)
 * val target = source.conv()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = A(...)
 * val target = source.conv(property = value)
 * ```
 * 
 * 
 * @see A
 * @see B
 */
public fun  snap.funname.A.conv(
    shared: String = this.shared,
    extra: Int,
) : snap.funname.B = snap.funname.B(
    shared = shared,
    extra = extra,
)

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * A -> C copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = A(...)
 * val target = source.conv()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = A(...)
 * val target = source.conv(property = value)
 * ```
 * 
 * 
 * @see A
 * @see C
 */
public fun  snap.funname.A.conv(
    shared: String = this.shared,
    flag: Boolean,
) : snap.funname.C = snap.funname.C(
    shared = shared,
    flag = flag,
)
````

## Input

```kt
package snap.funname

import me.tbsten.cream.CopyMapping

data class A(val shared: String)
data class B(val shared: String, val extra: Int)
data class C(val shared: String, val flag: Boolean)

@CopyMapping(A::class, B::class, funName = "conv")
@CopyMapping(A::class, C::class, funName = "conv")
object Mapping
```
