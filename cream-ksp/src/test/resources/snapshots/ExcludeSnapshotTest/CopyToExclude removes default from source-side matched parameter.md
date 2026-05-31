## Generated

````kt
// file: CopyTo__Source.kt
package snap.exclude

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
 * val target = source.copyToTarget(count = count)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(count = count, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.exclude.Source.copyToTarget(
    name: String = this.name,
    count: Int,
) : snap.exclude.Target = snap.exclude.Target(
    name = name,
    count = count,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
data class Source(
    val name: String,
    @CopyTo.Exclude val count: Int,
)

data class Target(val name: String, val count: Int)
```
