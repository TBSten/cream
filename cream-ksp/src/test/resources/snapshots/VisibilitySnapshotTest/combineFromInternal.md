## Generated

````kt
// file: CombineFrom__Source__Target.kt
package snap.visibility

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [Target])
 * 
 * [Source] -> [Target] copy function.
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

import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CopyVisibility

data class Source(
    val shared: String,
)

@CombineFrom(Source::class, visibility = CopyVisibility.INTERNAL)
data class Target(
    val shared: String,
    val extra: Int,
)
```
