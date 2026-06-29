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
 * Source -> Source.Child copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceChild()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceChild(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child
 */
public fun  me.tbsten.cream.generated.Source.copyToSourceChild(
    data: Data = this.data,
) : me.tbsten.cream.generated.Source.Child = me.tbsten.cream.generated.Source.Child(
    data = data,
)
````
