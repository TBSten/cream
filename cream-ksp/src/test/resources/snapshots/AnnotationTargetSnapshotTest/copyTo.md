## Generated

````kt
// file: CopyTo__Source.kt
package snap.annotation.copyTo

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
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
public fun  snap.annotation.copyTo.Source.copyToAnnTarget(
    x: Int = this.x,
) : snap.annotation.copyTo.AnnTarget = snap.annotation.copyTo.AnnTarget(
    x = x,
)
````

## Input

```kt
package snap.annotation.copyTo

import me.tbsten.cream.CopyTo

@CopyTo(AnnTarget::class)
data class Source(val x: Int)

annotation class AnnTarget(val x: Int)
```
