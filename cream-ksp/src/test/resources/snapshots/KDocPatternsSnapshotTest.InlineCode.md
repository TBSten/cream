## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.inlinecode

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * `isLoading` は常に `false` に確定する点に注意。
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
public fun  snap.kdoc.inlinecode.Source.copyToTarget(
    isLoading: Boolean = this.isLoading,
) : snap.kdoc.inlinecode.Target = snap.kdoc.inlinecode.Target(
    isLoading = isLoading,
)
````

## Input

```kt
package snap.kdoc.inlinecode

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(description = "`isLoading` は常に `false` に確定する点に注意。"),
)
data class Source(val isLoading: Boolean)

data class Target(val isLoading: Boolean)
```
