## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.threadsafety

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * 純粋な変換のみで副作用なし。任意のディスパッチャから安全に呼べる。
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
public fun  snap.kdoc.threadsafety.Source.copyToTarget(
    payload: String = this.payload,
) : snap.kdoc.threadsafety.Target = snap.kdoc.threadsafety.Target(
    payload = payload,
)
````

## Input

```kt
package snap.kdoc.threadsafety

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(description = "純粋な変換のみで副作用なし。任意のディスパッチャから安全に呼べる。"),
)
data class Source(val payload: String)

data class Target(val payload: String)
```
