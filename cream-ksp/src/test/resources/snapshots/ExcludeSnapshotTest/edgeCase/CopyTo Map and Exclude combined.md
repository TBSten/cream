## Generated

````kt
// file: CopyTo__Source.kt
package snap.exclude

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(renamed = renamed)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(renamed = renamed, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.exclude.Source.copyToTarget(
    renamed: String,
    keep: Int = this.keep,
) : snap.exclude.Target = snap.exclude.Target(
    renamed = renamed,
    keep = keep,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
data class Source(
    @CopyTo.Map("renamed") @CopyTo.Exclude val original: String,
    val keep: Int,
)

data class Target(val renamed: String, val keep: Int)
```
