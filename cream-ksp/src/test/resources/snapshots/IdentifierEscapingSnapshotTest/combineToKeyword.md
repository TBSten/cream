## Generated

````kt
// file: CombineTo__Primary__KwTarget.kt
package snap.escape.combineto

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [Primary])
 * 
 * [Primary] -> [KwTarget] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val primary = Primary(...)
 * val target = primary.copyToKwTarget(return = return)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val primary = Primary(...)
 * val target = primary.copyToKwTarget(return = return, property = value)
 * ```
 * 
 * 
 * @see Primary
 * @see KwTarget
 */
public fun  snap.escape.combineto.Primary.copyToKwTarget(
    `in`: Int = this.`in`,
    `return`: String,
) : snap.escape.combineto.KwTarget = snap.escape.combineto.KwTarget(
    `in` = `in`,
    `return` = `return`,
)
````

## Input

```kt
package snap.escape.combineto

import me.tbsten.cream.CombineTo

@CombineTo(KwTarget::class)
data class Primary(val `in`: Int)

data class Other(val `return`: String)

data class KwTarget(val `in`: Int, val `return`: String)
```
