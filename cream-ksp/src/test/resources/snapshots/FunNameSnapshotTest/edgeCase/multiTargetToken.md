## Generated

````kt
// file: CopyTo__Source.kt
package snap.funname

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Apple copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toApple()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toApple(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Apple
 */
public fun  snap.funname.Source.toApple(
    shared: String = this.shared,
    a: Int,
) : snap.funname.Apple = snap.funname.Apple(
    shared = shared,
    a = a,
)

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Banana copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toBanana()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toBanana(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Banana
 */
public fun  snap.funname.Source.toBanana(
    shared: String = this.shared,
    b: Int,
) : snap.funname.Banana = snap.funname.Banana(
    shared = shared,
    b = b,
)
````

## Input

```kt
package snap.funname

import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyTargetSimpleName

@CopyTo(Apple::class, Banana::class, funName = "to" + CopyTargetSimpleName)
data class Source(val shared: String)

data class Apple(val shared: String, val a: Int)
data class Banana(val shared: String, val b: Int)
```
