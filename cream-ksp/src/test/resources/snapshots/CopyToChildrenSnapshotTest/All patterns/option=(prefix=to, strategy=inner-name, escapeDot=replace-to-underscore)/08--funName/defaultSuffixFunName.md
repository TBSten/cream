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
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "inner-name")
    arg("escapeDot", "replace-to-underscore")
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
 * val target = source.to_DoneOrNull(data = data)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_DoneOrNull(data = data, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Done
 */
public fun  me.tbsten.cream.generated.Source.to_DoneOrNull(
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
 * val target = source.to_LoadingOrNull()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_LoadingOrNull(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Loading
 */
public fun  me.tbsten.cream.generated.Source.to_LoadingOrNull(
    id: String = this.id,
) : me.tbsten.cream.generated.Source.Loading = me.tbsten.cream.generated.Source.Loading(
    id = id,
)
````
