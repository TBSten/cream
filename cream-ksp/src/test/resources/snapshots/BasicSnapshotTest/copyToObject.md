## Generated

````kt
// file: CopyTo__Source.kt
package snap.basic.objecttarget

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
 * 
 * @see Source
 * @see Target
 */
public fun snap.basic.objecttarget.Source.copyToTarget() = snap.basic.objecttarget.Target
````

## Input

```kt
package snap.basic.objecttarget

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
data class Source(
    val shared: String,
    val onlyOnSource: Int,
)

object Target
```
