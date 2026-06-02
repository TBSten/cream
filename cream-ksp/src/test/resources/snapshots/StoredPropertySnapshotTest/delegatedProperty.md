## Generated

````kt
// file: CopyTo__Source.kt
package snap.stored.delegated

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
public fun  snap.stored.delegated.Source.copyToTarget(
    id: Int = this.id,
    name: String,
) : snap.stored.delegated.Target = snap.stored.delegated.Target(
    id = id,
    name = name,
)
````

## Input

```kt
package snap.stored.delegated

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
class Source(val id: Int) {
    val name: String by lazy { "name-$id" }
}

class Target(val id: Int, val name: String)
```
