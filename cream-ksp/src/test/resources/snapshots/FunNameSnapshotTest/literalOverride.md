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
 * val target = source.toState()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toState(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.funname.Source.toState(
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

@CopyTo(Target::class, funName = "toState")
data class Source(
    val shared: String,
)

data class Target(
    val shared: String,
    val extra: Int,
)
```
