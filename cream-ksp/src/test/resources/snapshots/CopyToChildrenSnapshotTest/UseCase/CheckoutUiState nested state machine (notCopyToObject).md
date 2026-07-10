## Input:me.tbsten.cream.generated.CheckoutUiState

```kt
package me.tbsten.cream.generated

import kotlin.String
import kotlin.collections.List
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface CheckoutUiState {
  public val sessionId: String

  public data object Idle : CheckoutUiState {
    override val sessionId: String
      get() = ""
  }

  public sealed interface Cart : CheckoutUiState {
    public val items: List<CartItem>

    public data class Editing(
      override val sessionId: String,
      override val items: List<CartItem>,
    ) : Cart

    public data class Validating(
      override val sessionId: String,
      override val items: List<CartItem>,
    ) : Cart
  }

  public sealed interface Payment : CheckoutUiState {
    public val items: List<CartItem>

    public val address: Address

    public data class SelectingMethod(
      override val sessionId: String,
      override val items: List<CartItem>,
      override val address: Address,
    ) : Payment

    public data class Processing(
      override val sessionId: String,
      override val items: List<CartItem>,
      override val address: Address,
      public val method: PaymentMethod,
    ) : Payment

    public data class Completed(
      override val sessionId: String,
      override val items: List<CartItem>,
      override val address: Address,
      public val orderId: String,
    ) : Payment
  }
}

public data class CartItem(
  public val name: String,
)

public data class Address(
  public val line: String,
)

public data class PaymentMethod(
  public val name: String,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "true")
    arg("defaultVisibility", "INHERIT" /* default */)
}
```

## Output:ExitCode

```kt
OK
```

## Output:Console

```kt

```

## Output:Generated sources

````kt
// file: CopyToChildren__CheckoutUiState.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [CheckoutUiState])
 * 
 * CheckoutUiState -> CheckoutUiState.Cart.Editing copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStateCartEditing(items = items)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStateCartEditing(items = items, property = value)
 * ```
 * 
 * 
 * @see CheckoutUiState
 * @see CheckoutUiState.Cart.Editing
 */
public fun  me.tbsten.cream.generated.CheckoutUiState.copyToCheckoutUiStateCartEditing(
    sessionId: String = this.sessionId,
    items: kotlin.collections.List<CartItem>,
) : me.tbsten.cream.generated.CheckoutUiState.Cart.Editing = me.tbsten.cream.generated.CheckoutUiState.Cart.Editing(
    sessionId = sessionId,
    items = items,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [CheckoutUiState])
 * 
 * CheckoutUiState -> CheckoutUiState.Cart.Validating copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStateCartValidating(items = items)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStateCartValidating(items = items, property = value)
 * ```
 * 
 * 
 * @see CheckoutUiState
 * @see CheckoutUiState.Cart.Validating
 */
public fun  me.tbsten.cream.generated.CheckoutUiState.copyToCheckoutUiStateCartValidating(
    sessionId: String = this.sessionId,
    items: kotlin.collections.List<CartItem>,
) : me.tbsten.cream.generated.CheckoutUiState.Cart.Validating = me.tbsten.cream.generated.CheckoutUiState.Cart.Validating(
    sessionId = sessionId,
    items = items,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [CheckoutUiState])
 * 
 * CheckoutUiState -> CheckoutUiState.Idle copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStateIdle()
 * ```
 * 
 * 
 * @see CheckoutUiState
 * @see CheckoutUiState.Idle
 */
public fun me.tbsten.cream.generated.CheckoutUiState.copyToCheckoutUiStateIdle() = me.tbsten.cream.generated.CheckoutUiState.Idle
/**
 * (Auto generate by @[CopyToChildren] annotation of [CheckoutUiState])
 * 
 * CheckoutUiState -> CheckoutUiState.Payment.Completed copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStatePaymentCompleted(items = items, address = address, orderId = orderId)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStatePaymentCompleted(items = items, address = address, orderId = orderId, property = value)
 * ```
 * 
 * 
 * @see CheckoutUiState
 * @see CheckoutUiState.Payment.Completed
 */
public fun  me.tbsten.cream.generated.CheckoutUiState.copyToCheckoutUiStatePaymentCompleted(
    sessionId: String = this.sessionId,
    items: kotlin.collections.List<CartItem>,
    address: Address,
    orderId: String,
) : me.tbsten.cream.generated.CheckoutUiState.Payment.Completed = me.tbsten.cream.generated.CheckoutUiState.Payment.Completed(
    sessionId = sessionId,
    items = items,
    address = address,
    orderId = orderId,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [CheckoutUiState])
 * 
 * CheckoutUiState -> CheckoutUiState.Payment.Processing copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStatePaymentProcessing(items = items, address = address, method = method)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStatePaymentProcessing(items = items, address = address, method = method, property = value)
 * ```
 * 
 * 
 * @see CheckoutUiState
 * @see CheckoutUiState.Payment.Processing
 */
public fun  me.tbsten.cream.generated.CheckoutUiState.copyToCheckoutUiStatePaymentProcessing(
    sessionId: String = this.sessionId,
    items: kotlin.collections.List<CartItem>,
    address: Address,
    method: PaymentMethod,
) : me.tbsten.cream.generated.CheckoutUiState.Payment.Processing = me.tbsten.cream.generated.CheckoutUiState.Payment.Processing(
    sessionId = sessionId,
    items = items,
    address = address,
    method = method,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [CheckoutUiState])
 * 
 * CheckoutUiState -> CheckoutUiState.Payment.SelectingMethod copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStatePaymentSelectingMethod(items = items, address = address)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = CheckoutUiState(...)
 * val target = source.copyToCheckoutUiStatePaymentSelectingMethod(items = items, address = address, property = value)
 * ```
 * 
 * 
 * @see CheckoutUiState
 * @see CheckoutUiState.Payment.SelectingMethod
 */
public fun  me.tbsten.cream.generated.CheckoutUiState.copyToCheckoutUiStatePaymentSelectingMethod(
    sessionId: String = this.sessionId,
    items: kotlin.collections.List<CartItem>,
    address: Address,
) : me.tbsten.cream.generated.CheckoutUiState.Payment.SelectingMethod = me.tbsten.cream.generated.CheckoutUiState.Payment.SelectingMethod(
    sessionId = sessionId,
    items = items,
    address = address,
)
````
