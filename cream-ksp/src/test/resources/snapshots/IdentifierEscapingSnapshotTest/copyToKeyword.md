## Generated

````kt
// file: CopyTo__KwSource.kt
package snap.escape.copyto

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [KwSource])
 * 
 * KwSource -> KwTarget copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = KwSource(...)
 * val target = source.copyToKwTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = KwSource(...)
 * val target = source.copyToKwTarget(property = value)
 * ```
 * 
 * 
 * @see KwSource
 * @see KwTarget
 */
public fun  snap.escape.copyto.KwSource.copyToKwTarget(
    `in`: Int = this.`in`,
    `is`: Boolean = this.`is`,
) : snap.escape.copyto.KwTarget = snap.escape.copyto.KwTarget(
    `in` = `in`,
    `is` = `is`,
)
````

## Input

```kt
package snap.escape.copyto

import me.tbsten.cream.CopyTo

@CopyTo(KwTarget::class)
data class KwSource(val `in`: Int, val `is`: Boolean)

data class KwTarget(val `in`: Int, val `is`: Boolean)
```
