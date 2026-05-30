## Generated

````kt
// file: CombineTo__CartState__Checkout.kt
package snap.kdoc.numberedlist

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [CartState])
 * 
 * [CartState] -> [Checkout] copy function.
 * 
 * カート確定の手順:
 * 1. CartState と PaymentInfo を用意する
 * 2. cart.copyToCheckout(paymentInfo = ...) を呼ぶ
 * 3. 戻り値を repository.submit() に渡す
 * 
 * # Example: Basic
 * 
 * ```kt
 * val cartState = CartState(...)
 * val target = cartState.copyToCheckout()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val cartState = CartState(...)
 * val target = cartState.copyToCheckout(property = value)
 * ```
 * 
 * 
 * @see CartState
 * @see Checkout
 */
public fun  snap.kdoc.numberedlist.CartState.copyToCheckout(
    total: Int = this.total,
    method: String,
) : snap.kdoc.numberedlist.Checkout = snap.kdoc.numberedlist.Checkout(
    total = total,
    method = method,
)
````

## Input

```kt
package snap.kdoc.numberedlist

import me.tbsten.cream.CombineTo
import me.tbsten.cream.KDoc

@CombineTo(
    Checkout::class,
    kdoc = KDoc(
        description = """
            カート確定の手順:
            1. CartState と PaymentInfo を用意する
            2. cart.copyToCheckout(paymentInfo = ...) を呼ぶ
            3. 戻り値を repository.submit() に渡す
        """,
    ),
)
data class CartState(val total: Int)

data class PaymentInfo(val method: String)

data class Checkout(val total: Int, val method: String)
```
