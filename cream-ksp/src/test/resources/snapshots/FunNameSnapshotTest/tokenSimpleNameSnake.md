## Generated

````kt
// file: CopyTo__Source.kt
package snap.funname

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.make_target()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.make_target(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.funname.Source.make_target(
    shared: String = this.shared,
    extra: Int,
) : snap.funname.Target = snap.funname.Target(
    shared = shared,
    extra = extra,
)
````

## Input

```kt
package snap.funname

import me.tbsten.cream.CopyTo
import me.tbsten.cream.copy_target_simple_name

@CopyTo(Target::class, funName = "make_" + copy_target_simple_name)
data class Source(
    val shared: String,
)

data class Target(
    val shared: String,
    val extra: Int,
)
```
