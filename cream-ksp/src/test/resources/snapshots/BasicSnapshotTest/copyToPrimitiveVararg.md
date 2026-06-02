## Generated

````kt
// file: CopyTo__Source.kt
package snap.basic.primitive

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
public fun  snap.basic.primitive.Source.copyToTarget(
    id: Int = this.id,
    vararg nums: Int = this.nums,
) : snap.basic.primitive.Target = snap.basic.primitive.Target(
    id = id,
    nums = nums,
)
````

## Input

```kt
package snap.basic.primitive

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
class Source(val id: Int, vararg val nums: Int)

class Target(val id: Int, vararg val nums: Int)
```
