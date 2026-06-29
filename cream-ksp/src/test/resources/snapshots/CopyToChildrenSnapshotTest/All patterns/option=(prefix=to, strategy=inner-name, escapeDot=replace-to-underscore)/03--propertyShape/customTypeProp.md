## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  public val `data`: Data

  public data class Child(
    override val `data`: Data,
  ) : Source
}

public data class Data(
  public val `value`: String,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "inner-name")
    arg("escapeDot", "replace-to-underscore")
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
 * Source -> Source.Child copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Child()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Child(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child
 */
public fun  me.tbsten.cream.generated.Source.to_Child(
    data: Data = this.data,
) : me.tbsten.cream.generated.Source.Child = me.tbsten.cream.generated.Source.Child(
    data = data,
)
````
