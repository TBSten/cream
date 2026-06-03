## Generated

````kt
// file: CopyFrom__KwTarget.kt
package snap.escape.copyfrom

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [KwTarget])
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
public fun  snap.escape.copyfrom.KwSource.copyToKwTarget(
    `object`: String = this.`object`,
) : snap.escape.copyfrom.KwTarget = snap.escape.copyfrom.KwTarget(
    `object` = `object`,
)
````

## Input

```kt
package snap.escape.copyfrom

import me.tbsten.cream.CopyFrom

data class KwSource(val `object`: String)

@CopyFrom(KwSource::class)
data class KwTarget(val `object`: String)
```
