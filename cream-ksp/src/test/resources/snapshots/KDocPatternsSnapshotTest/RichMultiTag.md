## Generated

````kt
// file: CopyTo__OrderCart.kt
package snap.kdoc.richmultitag

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [OrderCart])
 * 
 * OrderCart -> OrderConfirmed copy function.
 * 
 * カート確定処理。在庫確保後にこの遷移を行うこと。
 * 
 * @param paidAt 決済完了時刻。
 * @return 確定済みの注文。以降キャンセルは別フローになる。
 * @throws IllegalStateException カートが空の場合。
 * @see copyToOrderDraft
 * @sample com.example.samples.confirmOrderSample
 * @since 1.4.0
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = OrderCart(...)
 * val target = source.copyToOrderConfirmed()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = OrderCart(...)
 * val target = source.copyToOrderConfirmed(property = value)
 * ```
 * 
 * 
 * @see OrderCart
 * @see OrderConfirmed
 */
public fun  snap.kdoc.richmultitag.OrderCart.copyToOrderConfirmed(
    items: kotlin.collections.List<String> = this.items,
    paidAt: String,
) : snap.kdoc.richmultitag.OrderConfirmed = snap.kdoc.richmultitag.OrderConfirmed(
    items = items,
    paidAt = paidAt,
)
````

## Input

```kt
package snap.kdoc.richmultitag

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    OrderConfirmed::class,
    kdoc = KDoc(
        description = """
            カート確定処理。在庫確保後にこの遷移を行うこと。

            @param paidAt 決済完了時刻。
            @return 確定済みの注文。以降キャンセルは別フローになる。
            @throws IllegalStateException カートが空の場合。
            @see copyToOrderDraft
            @sample com.example.samples.confirmOrderSample
            @since 1.4.0
        """,
    ),
)
data class OrderCart(val items: List<String>)

data class OrderConfirmed(val items: List<String>, val paidAt: String)
```
