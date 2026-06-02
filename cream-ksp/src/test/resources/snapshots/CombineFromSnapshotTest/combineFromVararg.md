## Generated

````kt
// file: CombineFrom__Primary__Target.kt
package snap.combinefrom.vararg

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [Target])
 * 
 * [Primary] + [Other] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val primary = Primary(...)
 * val other = Other(...)
 * val target = primary.copyToTarget(other = Other(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val primary = Primary(...)
 * val other = Other(...)
 * val target = primary.copyToTarget(other = Other(...), property = value)
 * ```
 * 
 * 
 * @see Primary
 * @see Other
 * @see Target
 */
public fun  snap.combinefrom.vararg.Primary.copyToTarget(
    other: snap.combinefrom.vararg.Other,
    id: Int = this.id,
    extra: String = other.extra,
    vararg tags: String = this.tags,
) : snap.combinefrom.vararg.Target = snap.combinefrom.vararg.Target(
    id = id,
    extra = extra,
    tags = tags,
)
````

## Input

```kt
package snap.combinefrom.vararg

import me.tbsten.cream.CombineFrom

class Primary(val id: Int, vararg val tags: String)

class Other(val extra: String)

@CombineFrom(Primary::class, Other::class)
class Target(val id: Int, val extra: String, vararg val tags: String)
```
