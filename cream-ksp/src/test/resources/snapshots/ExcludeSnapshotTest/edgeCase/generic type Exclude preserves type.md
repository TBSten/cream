## Generated

````kt
// file: CopyFrom__Result.Success.kt
package snap.exclude

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Result.Success])
 * 
 * Result -> Result.Success copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Result(...)
 * val target = source.copyToResultSuccess(items = items)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Result(...)
 * val target = source.copyToResultSuccess(items = items, property = value)
 * ```
 * 
 * 
 * @see Result
 * @see Result.Success
 */
public fun <E : Any?> snap.exclude.Result<E>.copyToResultSuccess(
    label: String = this.label,
    items: kotlin.collections.List<E>,
) : snap.exclude.Result.Success<E> = snap.exclude.Result.Success(
    label = label,
    items = items,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyFrom

sealed interface Result<E> {
    val items: List<E>
    val label: String

    @CopyFrom(Result::class)
    data class Success<E>(
        override val label: String,
        @CopyFrom.Exclude override val items: List<E>,
    ) : Result<E>
}
```
