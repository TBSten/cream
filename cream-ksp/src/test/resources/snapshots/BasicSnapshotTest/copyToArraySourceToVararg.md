## Generated

````kt
// file: CopyTo__Source.kt
package snap.basic.arraysource

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
public fun  snap.basic.arraysource.Source.copyToTarget(
    id: Int = this.id,
    vararg tags: String = this.tags,
) : snap.basic.arraysource.Target = snap.basic.arraysource.Target(
    id = id,
    tags = tags,
)
````

## Input

```kt
package snap.basic.arraysource

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
class Source(val id: Int, val tags: Array<String>)

class Target(val id: Int, vararg val tags: String)
```
