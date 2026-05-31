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
 * val target = source.makeTarget(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.makeTarget(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.funname.Source.makeTarget(
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
import me.tbsten.cream.CopyTargetSimpleName

@CopyTo(Target::class, funName = "make" + CopyTargetSimpleName)
data class Source(
    val shared: String,
)

data class Target(
    val shared: String,
    val extra: Int,
)
```
