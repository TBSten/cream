## Generated

````kt
// file: CopyFrom__Target.kt
package snap.funname

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Target])
 * 
 * Source -> Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toTarget(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toTarget(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.funname.Source.toTarget(
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

import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTargetSimpleName

data class Source(val shared: String)

@CopyFrom(Source::class, funName = "to" + CopyTargetSimpleName)
data class Target(val shared: String, val extra: Int)
```
