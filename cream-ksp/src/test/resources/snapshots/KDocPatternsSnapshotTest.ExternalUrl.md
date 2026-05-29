## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.externalurl

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * リクエスト仕様は [API ドキュメント](https://example.com/docs/orders) を参照。
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
public fun  snap.kdoc.externalurl.Source.copyToTarget(
    amount: Int = this.amount,
) : snap.kdoc.externalurl.Target = snap.kdoc.externalurl.Target(
    amount = amount,
)
````

## Input

```kt
package snap.kdoc.externalurl

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(description = "リクエスト仕様は [API ドキュメント](https://example.com/docs/orders) を参照。"),
)
data class Source(val amount: Int)

data class Target(val amount: Int)
```
