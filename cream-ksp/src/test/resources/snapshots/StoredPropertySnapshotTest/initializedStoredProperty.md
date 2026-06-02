## Generated

````kt
// file: CopyTo__Source.kt
package snap.stored.initialized

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
public fun  snap.stored.initialized.Source.copyToTarget(
    id: Int = this.id,
    name: String = this.name,
) : snap.stored.initialized.Target = snap.stored.initialized.Target(
    id = id,
    name = name,
)
````

## Input

```kt
package snap.stored.initialized

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
class Source(val id: Int) {
    val name: String = "fixed"
}

class Target(val id: Int, val name: String)
```
