## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.DefaultCopyFunctionName

@CopyToChildren(funName = DefaultCopyFunctionName + "OrNull")
public sealed interface Source {
  public val id: String

  public data class Loading(
    override val id: String,
  ) : Source

  public data class Done(
    override val id: String,
    public val `data`: String,
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
 * Source -> Source.Done copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceDoneOrNull(data = data)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceDoneOrNull(data = data, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Done
 */
internal fun  me.tbsten.cream.generated.Source.copyToSourceDoneOrNull(
    id: String = this.id,
    data: String,
) : me.tbsten.cream.generated.Source.Done = me.tbsten.cream.generated.Source.Done(
    id = id,
    data = data,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.Loading copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceLoadingOrNull()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceLoadingOrNull(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Loading
 */
internal fun  me.tbsten.cream.generated.Source.copyToSourceLoadingOrNull(
    id: String = this.id,
) : me.tbsten.cream.generated.Source.Loading = me.tbsten.cream.generated.Source.Loading(
    id = id,
)
````
