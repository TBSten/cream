## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.atparam

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * @param data API から取得したアイテム詳細。null 不可。
 * @param itemId 省略時は遷移元の itemId を引き継ぐ。
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
public fun  snap.kdoc.atparam.Source.copyToTarget(
    itemId: String = this.itemId,
    data: String,
) : snap.kdoc.atparam.Target = snap.kdoc.atparam.Target(
    itemId = itemId,
    data = data,
)
````

## Input

```kt
package snap.kdoc.atparam

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(
        description = """
            @param data API から取得したアイテム詳細。null 不可。
            @param itemId 省略時は遷移元の itemId を引き継ぐ。
        """,
    ),
)
data class Source(val itemId: String)

data class Target(val itemId: String, val data: String)
```
