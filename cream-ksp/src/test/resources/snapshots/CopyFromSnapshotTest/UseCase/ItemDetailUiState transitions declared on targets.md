## Input:me.tbsten.cream.generated.ItemDetailUiState

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.String
import me.tbsten.cream.CopyFrom

public sealed interface ItemDetailUiState {
  public val itemId: String

  public val isBookmarked: Boolean

  public val snackbarMessage: String?

  public data class Loading(
    override val itemId: String,
    override val isBookmarked: Boolean,
    override val snackbarMessage: String?,
  ) : ItemDetailUiState

  @CopyFrom(Loading::class)
  public data class Success(
    override val itemId: String,
    public val item: Item,
    override val isBookmarked: Boolean,
    override val snackbarMessage: String?,
  ) : ItemDetailUiState

  @CopyFrom(Loading::class)
  public data class Error(
    override val itemId: String,
    public val message: String,
    override val isBookmarked: Boolean,
    override val snackbarMessage: String?,
  ) : ItemDetailUiState
}

public data class Item(
  public val id: String,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
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
// file: CopyFrom__ItemDetailUiState.Error.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [ItemDetailUiState.Error])
 * 
 * ItemDetailUiState.Loading -> ItemDetailUiState.Error copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Loading(...)
 * val target = source.copyToItemDetailUiStateError(message = message)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Loading(...)
 * val target = source.copyToItemDetailUiStateError(message = message, property = value)
 * ```
 * 
 * 
 * @see ItemDetailUiState.Loading
 * @see ItemDetailUiState.Error
 */
public fun  me.tbsten.cream.generated.ItemDetailUiState.Loading.copyToItemDetailUiStateError(
    itemId: String = this.itemId,
    message: String,
    isBookmarked: Boolean = this.isBookmarked,
    snackbarMessage: String? = this.snackbarMessage,
) : me.tbsten.cream.generated.ItemDetailUiState.Error = me.tbsten.cream.generated.ItemDetailUiState.Error(
    itemId = itemId,
    message = message,
    isBookmarked = isBookmarked,
    snackbarMessage = snackbarMessage,
)

// ----- next file -----

// file: CopyFrom__ItemDetailUiState.Success.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [ItemDetailUiState.Success])
 * 
 * ItemDetailUiState.Loading -> ItemDetailUiState.Success copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Loading(...)
 * val target = source.copyToItemDetailUiStateSuccess(item = item)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Loading(...)
 * val target = source.copyToItemDetailUiStateSuccess(item = item, property = value)
 * ```
 * 
 * 
 * @see ItemDetailUiState.Loading
 * @see ItemDetailUiState.Success
 */
public fun  me.tbsten.cream.generated.ItemDetailUiState.Loading.copyToItemDetailUiStateSuccess(
    itemId: String = this.itemId,
    item: Item,
    isBookmarked: Boolean = this.isBookmarked,
    snackbarMessage: String? = this.snackbarMessage,
) : me.tbsten.cream.generated.ItemDetailUiState.Success = me.tbsten.cream.generated.ItemDetailUiState.Success(
    itemId = itemId,
    item = item,
    isBookmarked = isBookmarked,
    snackbarMessage = snackbarMessage,
)
````
