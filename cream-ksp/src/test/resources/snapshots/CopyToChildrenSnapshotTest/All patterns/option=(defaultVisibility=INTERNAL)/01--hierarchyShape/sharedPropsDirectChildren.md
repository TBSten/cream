## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  public val `value`: String

  public data class Loading(
    override val `value`: String,
  ) : Source

  public data class Success(
    override val `value`: String,
    public val count: Int,
  ) : Source
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INTERNAL")
    arg("nonCopyableStrategy", "INHERIT" /* default */)
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
// file: CopyToChildren__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.Loading copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceLoading()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceLoading(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Loading
 */
internal fun  me.tbsten.cream.generated.Source.copyToSourceLoading(
    value: String = this.value,
) : me.tbsten.cream.generated.Source.Loading = me.tbsten.cream.generated.Source.Loading(
    value = value,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.Success copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceSuccess(count = count)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceSuccess(count = count, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Success
 */
internal fun  me.tbsten.cream.generated.Source.copyToSourceSuccess(
    value: String = this.value,
    count: Int,
) : me.tbsten.cream.generated.Source.Success = me.tbsten.cream.generated.Source.Success(
    value = value,
    count = count,
)
````
