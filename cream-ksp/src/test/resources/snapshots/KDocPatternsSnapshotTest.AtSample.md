## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.atsample

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * @sample com.example.samples.cartSummarySample
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
public fun  snap.kdoc.atsample.Source.copyToTarget(
    items: kotlin.collections.List<String> = this.items,
) : snap.kdoc.atsample.Target = snap.kdoc.atsample.Target(
    items = items,
)
````

## Input

```kt
package snap.kdoc.atsample

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(description = "@sample com.example.samples.cartSummarySample"),
)
data class Source(val items: List<String>)

data class Target(val items: List<String>)
```
