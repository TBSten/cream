## Generated

````kt
// file: CopyTo__Source.kt
package snap.basic.nomatch

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
 * val target = source.copyToTarget(tags = tags)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(tags = tags, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.basic.nomatch.Source.copyToTarget(
    id: Int = this.id,
    vararg tags: String,
) : snap.basic.nomatch.Target = snap.basic.nomatch.Target(
    id = id,
    tags = tags,
)
````

## Input

```kt
package snap.basic.nomatch

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
class Source(val id: Int)

class Target(val id: Int, vararg val tags: String)
```
