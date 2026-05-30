## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.todonote

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * TODO: ネストした metadata の深いコピーは未対応。浅いコピーになる。
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
public fun  snap.kdoc.todonote.Source.copyToTarget(
    metadata: kotlin.collections.Map<String, Any> = this.metadata,
) : snap.kdoc.todonote.Target = snap.kdoc.todonote.Target(
    metadata = metadata,
)
````

## Input

```kt
package snap.kdoc.todonote

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(description = "TODO: ネストした metadata の深いコピーは未対応。浅いコピーになる。"),
)
data class Source(val metadata: Map<String, Any>)

data class Target(val metadata: Map<String, Any>)
```
