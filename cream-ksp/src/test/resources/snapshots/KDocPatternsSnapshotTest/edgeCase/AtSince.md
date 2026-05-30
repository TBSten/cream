## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.atsince

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * @since 2.3.0 API v2 移行に伴い追加。
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
public fun  snap.kdoc.atsince.Source.copyToTarget(
    body: String = this.body,
) : snap.kdoc.atsince.Target = snap.kdoc.atsince.Target(
    body = body,
)
````

## Input

```kt
package snap.kdoc.atsince

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(description = "@since 2.3.0 API v2 移行に伴い追加。"),
)
data class Source(val body: String)

data class Target(val body: String)
```
