## Generated

````kt
// file: CopyFrom__Target.kt
package snap.visibility

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Target])
 * 
 * Source -> Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
internal fun  snap.visibility.Source.copyToTarget(
    shared: String = this.shared,
    extra: Int,
) : snap.visibility.Target = snap.visibility.Target(
    shared = shared,
    extra = extra,
)
````

## Input

```kt
package snap.visibility

import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyVisibility

data class Source(
    val shared: String,
)

@CopyFrom(Source::class, visibility = CopyVisibility.INTERNAL)
data class Target(
    val shared: String,
    val extra: Int,
)
```
