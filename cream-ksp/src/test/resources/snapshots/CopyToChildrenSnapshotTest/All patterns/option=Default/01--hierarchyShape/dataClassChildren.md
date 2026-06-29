## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  public data class Loading(
    public val a: String,
  ) : Source

  public data class Success(
    public val a: String,
    public val b: Int,
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
 * val target = source.copyToSourceLoading(a = a)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceLoading(a = a, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Loading
 */
public fun  me.tbsten.cream.generated.Source.copyToSourceLoading(
    a: String,
) : me.tbsten.cream.generated.Source.Loading = me.tbsten.cream.generated.Source.Loading(
    a = a,
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
 * val target = source.copyToSourceSuccess(a = a, b = b)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceSuccess(a = a, b = b, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Success
 */
public fun  me.tbsten.cream.generated.Source.copyToSourceSuccess(
    a: String,
    b: Int,
) : me.tbsten.cream.generated.Source.Success = me.tbsten.cream.generated.Source.Success(
    a = a,
    b = b,
)
````
