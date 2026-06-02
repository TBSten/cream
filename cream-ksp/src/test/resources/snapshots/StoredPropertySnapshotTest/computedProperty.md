## Generated

````kt
// file: CopyTo__Source.kt
package snap.stored.computed

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
 * val target = source.copyToTarget(name = name)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(name = name, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.stored.computed.Source.copyToTarget(
    id: Int = this.id,
    name: String,
) : snap.stored.computed.Target = snap.stored.computed.Target(
    id = id,
    name = name,
)
````

## Input

```kt
package snap.stored.computed

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
class Source(val id: Int) {
    val name: String
        get() = "name-$id"
}

class Target(val id: Int, val name: String)
```
