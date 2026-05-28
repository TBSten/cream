## Generated

````kt
// file: CopyFrom__Target.kt
package snap.kind.copyFromBasic

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
 * val target = source.copyToTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.kind.copyFromBasic.Source.copyToTarget(
    shared: String = this.shared,
    onlyOnTarget: Boolean,
) : snap.kind.copyFromBasic.Target = snap.kind.copyFromBasic.Target(
    shared = shared,
    onlyOnTarget = onlyOnTarget,
)
````

## Input

```kt
package snap.kind.copyFromBasic

import me.tbsten.cream.CopyFrom

data class Source(
    val shared: String,
    val onlyOnSource: Int,
)

@CopyFrom(Source::class)
data class Target(
    val shared: String,
    val onlyOnTarget: Boolean,
)
```
