## Generated

````kt
// file: CombineFrom__Alpha__Target.kt
package snap.combinefrom.distinct

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [Target])
 * 
 * [Alpha] + [Beta] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val alpha = Alpha(...)
 * val beta = Beta(...)
 * val target = alpha.copyToTarget(beta = Beta(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val alpha = Alpha(...)
 * val beta = Beta(...)
 * val target = alpha.copyToTarget(beta = Beta(...), property = value)
 * ```
 * 
 * 
 * @see Alpha
 * @see Beta
 * @see Target
 */
public fun  snap.combinefrom.distinct.Alpha.copyToTarget(
    beta: snap.combinefrom.distinct.Beta,
    id: String = this.id,
    count: Int = beta.count,
) : snap.combinefrom.distinct.Target = snap.combinefrom.distinct.Target(
    id = id,
    count = count,
)
````

## Input

```kt
package snap.combinefrom.distinct

import me.tbsten.cream.CombineFrom

data class Alpha(val id: String)
data class Beta(val count: Int)

@CombineFrom(Alpha::class)
@CombineFrom(Beta::class)
data class Target(val id: String, val count: Int)
```
