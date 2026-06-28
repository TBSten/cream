## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyTargetSimpleName
import me.tbsten.cream.CopyToChildren

@CopyToChildren(funName = "into" + CopyTargetSimpleName)
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
 * val target = source.intoDone(data = data)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.intoDone(data = data, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Done
 */
public fun  me.tbsten.cream.generated.Source.intoDone(
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
 * val target = source.intoLoading()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.intoLoading(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Loading
 */
public fun  me.tbsten.cream.generated.Source.intoLoading(
    id: String = this.id,
) : me.tbsten.cream.generated.Source.Loading = me.tbsten.cream.generated.Source.Loading(
    id = id,
)
````
