## Generated

````kt
// file: CombineTo__Source__Target.kt
package snap.funname

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [Source])
 * 
 * [Source] -> [Target] copy function.
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

import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyTargetSimpleName

@CombineTo(Target::class, funName = "to" + CopyTargetSimpleName)
data class Source(val shared: String)

data class Target(val shared: String, val extra: Int)
```
