## Generated

````kt
// file: CopyTo__Source.kt
package snap.stored.lateinit

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
public fun  snap.stored.lateinit.Source.copyToTarget(
    id: Int = this.id,
    name: String,
) : snap.stored.lateinit.Target = snap.stored.lateinit.Target(
    id = id,
    name = name,
)
````

## Input

```kt
package snap.stored.lateinit

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
class Source(val id: Int) {
    lateinit var name: String
}

class Target(val id: Int, val name: String)
```
