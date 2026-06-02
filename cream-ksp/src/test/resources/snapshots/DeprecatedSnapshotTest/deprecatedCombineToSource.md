## Generated

````kt
// file: CombineTo__Primary__Target.kt
package snap.deprecated.combineto

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [Primary])
 * 
 * [Primary] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val primary = Primary(...)
 * val target = primary.copyToTarget(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val primary = Primary(...)
 * val target = primary.copyToTarget(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Primary
 * @see Target
 */
@Deprecated("primary is deprecated")
public fun  snap.deprecated.combineto.Primary.copyToTarget(
    id: Int = this.id,
    extra: String,
) : snap.deprecated.combineto.Target = snap.deprecated.combineto.Target(
    id = id,
    extra = extra,
)
````

## Input

```kt
package snap.deprecated.combineto

import me.tbsten.cream.CombineTo

@Deprecated("primary is deprecated")
@CombineTo(Target::class)
data class Primary(val id: Int)

data class Other(val extra: String)

data class Target(val id: Int, val extra: String)
```
