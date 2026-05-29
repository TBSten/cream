## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.atsee

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * @see copyToLoading 逆方向の遷移にはこちらを使う。
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
public fun  snap.kdoc.atsee.Source.copyToTarget(
    shared: String = this.shared,
) : snap.kdoc.atsee.Target = snap.kdoc.atsee.Target(
    shared = shared,
)
````

## Input

```kt
package snap.kdoc.atsee

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(description = "@see copyToLoading 逆方向の遷移にはこちらを使う。"),
)
data class Source(val shared: String)

data class Target(val shared: String)
```
