## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.atsuppress

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * @suppress 内部遷移用。公開ドキュメントには出さない。
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
public fun  snap.kdoc.atsuppress.Source.copyToTarget(
    shared: String = this.shared,
) : snap.kdoc.atsuppress.Target = snap.kdoc.atsuppress.Target(
    shared = shared,
)
````

## Input

```kt
package snap.kdoc.atsuppress

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(description = "@suppress 内部遷移用。公開ドキュメントには出さない。"),
)
data class Source(val shared: String)

data class Target(val shared: String)
```
