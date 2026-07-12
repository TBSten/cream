## Input:me.tbsten.cream.generated.SearchState

```kt
package me.tbsten.cream.generated

import java.time.Instant
import kotlin.String
import kotlin.collections.List
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface SearchState : State {
  public val query: String

  public val sortOrder: SortOrder

  public val searchedAt: Instant?

  public data class Loading(
    override val query: String,
    override val sortOrder: SortOrder,
    override val searchedAt: Instant?,
  ) : SearchState

  public data class Content(
    override val query: String,
    override val sortOrder: SortOrder,
    override val searchedAt: Instant?,
    public val results: List<Item>,
  ) : SearchState

  public data class Error(
    override val query: String,
    override val sortOrder: SortOrder,
    override val searchedAt: Instant?,
    public val message: String,
  ) : SearchState
}

public interface State

public data class Item(
  public val id: String,
)

public class SortOrder(
  public val key: String,
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
// file: CopyToChildren__SearchState.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [SearchState])
 * 
 * SearchState -> SearchState.Content copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = SearchState(...)
 * val target = source.copyToSearchStateContent(results = results)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = SearchState(...)
 * val target = source.copyToSearchStateContent(results = results, property = value)
 * ```
 * 
 * 
 * @see SearchState
 * @see SearchState.Content
 */
public fun  me.tbsten.cream.generated.SearchState.copyToSearchStateContent(
    query: String = this.query,
    sortOrder: SortOrder = this.sortOrder,
    searchedAt: java.time.Instant? = this.searchedAt,
    results: kotlin.collections.List<Item>,
) : me.tbsten.cream.generated.SearchState.Content = me.tbsten.cream.generated.SearchState.Content(
    query = query,
    sortOrder = sortOrder,
    searchedAt = searchedAt,
    results = results,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [SearchState])
 * 
 * SearchState -> SearchState.Error copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = SearchState(...)
 * val target = source.copyToSearchStateError(message = message)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = SearchState(...)
 * val target = source.copyToSearchStateError(message = message, property = value)
 * ```
 * 
 * 
 * @see SearchState
 * @see SearchState.Error
 */
public fun  me.tbsten.cream.generated.SearchState.copyToSearchStateError(
    query: String = this.query,
    sortOrder: SortOrder = this.sortOrder,
    searchedAt: java.time.Instant? = this.searchedAt,
    message: String,
) : me.tbsten.cream.generated.SearchState.Error = me.tbsten.cream.generated.SearchState.Error(
    query = query,
    sortOrder = sortOrder,
    searchedAt = searchedAt,
    message = message,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [SearchState])
 * 
 * SearchState -> SearchState.Loading copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = SearchState(...)
 * val target = source.copyToSearchStateLoading()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = SearchState(...)
 * val target = source.copyToSearchStateLoading(property = value)
 * ```
 * 
 * 
 * @see SearchState
 * @see SearchState.Loading
 */
public fun  me.tbsten.cream.generated.SearchState.copyToSearchStateLoading(
    query: String = this.query,
    sortOrder: SortOrder = this.sortOrder,
    searchedAt: java.time.Instant? = this.searchedAt,
) : me.tbsten.cream.generated.SearchState.Loading = me.tbsten.cream.generated.SearchState.Loading(
    query = query,
    sortOrder = sortOrder,
    searchedAt = searchedAt,
)
````
