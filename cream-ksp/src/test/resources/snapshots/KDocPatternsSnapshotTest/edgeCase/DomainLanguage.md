## Generated

````kt
// file: CopyFrom__注文.kt
package snap.kdoc.domainlanguage

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [注文])
 * 
 * 注文Entity -> 注文 copy function.
 * 
 * DB の注文レコードを業務ドメインの注文集約へ変換する。金額は税込で保持する。
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = 注文Entity(...)
 * val target = source.copyTo注文()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = 注文Entity(...)
 * val target = source.copyTo注文(property = value)
 * ```
 * 
 * 
 * @see 注文Entity
 * @see 注文
 */
public fun  snap.kdoc.domainlanguage.注文Entity.copyTo注文(
    税込金額: Int = this.税込金額,
) : snap.kdoc.domainlanguage.注文 = snap.kdoc.domainlanguage.注文(
    税込金額 = 税込金額,
)
````

## Input

```kt
package snap.kdoc.domainlanguage

import me.tbsten.cream.CopyFrom
import me.tbsten.cream.KDoc

data class 注文Entity(val 税込金額: Int)

@CopyFrom(
    注文Entity::class,
    kdoc = KDoc(description = "DB の注文レコードを業務ドメインの注文集約へ変換する。金額は税込で保持する。"),
)
data class 注文(val 税込金額: Int)
```
