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
    arg("copyFunNamingStrategy", "inner-name")
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "true")
    arg("defaultVisibility", "INTERNAL")
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
 * val target = source.copyToLoading()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToLoading(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Loading
 */
internal fun  me.tbsten.cream.generated.Source.copyToLoading(
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
 * val target = source.copyToSuccess(count = count)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSuccess(count = count, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Success
 */
internal fun  me.tbsten.cream.generated.Source.copyToSuccess(
    value: String = this.value,
    count: Int,
) : me.tbsten.cream.generated.Source.Success = me.tbsten.cream.generated.Source.Success(
    value = value,
    count = count,
)
````
