## Generated

````kt
// file: CopyTo__Source.kt
package snap.basic.nullableprimitivearray

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
 * val target = source.copyToTarget(nums = nums)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(nums = nums, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.basic.nullableprimitivearray.Source.copyToTarget(
    id: Int = this.id,
    vararg nums: Int,
) : snap.basic.nullableprimitivearray.Target = snap.basic.nullableprimitivearray.Target(
    id = id,
    nums = nums,
)
````

## Input

```kt
package snap.basic.nullableprimitivearray

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
class Source(val id: Int, val nums: IntArray?)

class Target(val id: Int, vararg val nums: Int)
```
