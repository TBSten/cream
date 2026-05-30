## Generated

````kt
// file: CopyFrom__Contract.kt
package snap.kdoc.domaininvariant

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Contract])
 * 
 * Quote -> Contract copy function.
 * 
 * 確定見積から契約を生成する。契約金額は見積金額と必ず一致する（値引きは別途）。
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Quote(...)
 * val target = source.copyToContract()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Quote(...)
 * val target = source.copyToContract(property = value)
 * ```
 * 
 * 
 * @see Quote
 * @see Contract
 */
public fun  snap.kdoc.domaininvariant.Quote.copyToContract(
    amount: Int = this.amount,
) : snap.kdoc.domaininvariant.Contract = snap.kdoc.domaininvariant.Contract(
    amount = amount,
)
````

## Input

```kt
package snap.kdoc.domaininvariant

import me.tbsten.cream.CopyFrom
import me.tbsten.cream.KDoc

data class Quote(val amount: Int)

@CopyFrom(
    Quote::class,
    kdoc = KDoc(description = "確定見積から契約を生成する。契約金額は見積金額と必ず一致する（値引きは別途）。"),
)
data class Contract(val amount: Int)
```
