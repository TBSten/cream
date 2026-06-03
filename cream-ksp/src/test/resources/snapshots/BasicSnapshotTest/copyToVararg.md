## Generated

````kt
// file: CopyTo__Source.kt
package snap.basic.vararg

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
public fun  snap.basic.vararg.Source.copyToTarget(
    id: Int = this.id,
    vararg tags: String = this.tags,
) : snap.basic.vararg.Target = snap.basic.vararg.Target(
    id = id,
    tags = tags,
)
````

## Input

```kt
package snap.basic.vararg

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
class Source(val id: Int, vararg val tags: String)

class Target(val id: Int, vararg val tags: String)
```
