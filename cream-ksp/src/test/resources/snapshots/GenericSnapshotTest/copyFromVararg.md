## Generated

````kt
// file: CopyFrom__Target.kt
package snap.generic.vararg

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Target])
 * 
 * Source -> Target copy function.
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
public fun  snap.generic.vararg.Source.copyToTarget(
    id: Int = this.id,
    vararg tags: String = this.tags,
) : snap.generic.vararg.Target = snap.generic.vararg.Target(
    id = id,
    tags = tags,
)
````

## Input

```kt
package snap.generic.vararg

import me.tbsten.cream.CopyFrom

class Source(val id: Int, vararg val tags: String)

@CopyFrom(Source::class)
class Target(val id: Int, vararg val tags: String)
```
