## Generated

````kt
// file: CopyFrom__AnnTarget.kt
package snap.annotation.copyFrom

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [AnnTarget])
 * 
 * Source -> AnnTarget copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToAnnTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToAnnTarget(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see AnnTarget
 */
public fun  snap.annotation.copyFrom.Source.copyToAnnTarget(
    x: Int = this.x,
) : snap.annotation.copyFrom.AnnTarget = snap.annotation.copyFrom.AnnTarget(
    x = x,
)
````

## Input

```kt
package snap.annotation.copyFrom

import me.tbsten.cream.CopyFrom

data class Source(val x: Int)

@CopyFrom(Source::class)
annotation class AnnTarget(val x: Int)
```
