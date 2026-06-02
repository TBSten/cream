## Generated

````kt
// file: CopyTo__SpSource.kt
package snap.escape.space

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [SpSource])
 * 
 * SpSource -> SpTarget copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = SpSource(...)
 * val target = source.copyToSpTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = SpSource(...)
 * val target = source.copyToSpTarget(property = value)
 * ```
 * 
 * 
 * @see SpSource
 * @see SpTarget
 */
public fun  snap.escape.space.SpSource.copyToSpTarget(
    `my prop`: Int = this.`my prop`,
) : snap.escape.space.SpTarget = snap.escape.space.SpTarget(
    `my prop` = `my prop`,
)
````

## Input

```kt
package snap.escape.space

import me.tbsten.cream.CopyTo

@CopyTo(SpTarget::class)
data class SpSource(val `my prop`: Int)

data class SpTarget(val `my prop`: Int)
```
