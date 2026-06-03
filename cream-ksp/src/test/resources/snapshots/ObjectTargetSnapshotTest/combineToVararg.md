## Generated

````kt
// file: CombineTo__Primary__Target.kt
package snap.objtarget.vararg

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [Primary])
 * 
 * [Primary] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val primary = Primary(...)
 * val target = primary.copyToTarget(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val primary = Primary(...)
 * val target = primary.copyToTarget(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Primary
 * @see Target
 */
public fun  snap.objtarget.vararg.Primary.copyToTarget(
    id: Int = this.id,
    extra: String,
    vararg tags: String = this.tags,
) : snap.objtarget.vararg.Target = snap.objtarget.vararg.Target(
    id = id,
    extra = extra,
    tags = tags,
)
````

## Input

```kt
package snap.objtarget.vararg

import me.tbsten.cream.CombineTo

@CombineTo(Target::class)
class Primary(val id: Int, vararg val tags: String)

class Other(val extra: String)

class Target(val id: Int, val extra: String, vararg val tags: String)
```
