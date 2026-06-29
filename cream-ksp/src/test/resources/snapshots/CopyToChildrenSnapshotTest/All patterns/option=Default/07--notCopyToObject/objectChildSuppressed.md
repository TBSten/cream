## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren(notCopyToObject = true)
public sealed interface Source {
  public data class DataChild(
    public val a: String,
  ) : Source

  public object ObjectChild : Source
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
 * Source -> Source.DataChild copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceDataChild(a = a)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceDataChild(a = a, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.DataChild
 */
public fun  me.tbsten.cream.generated.Source.copyToSourceDataChild(
    a: String,
) : me.tbsten.cream.generated.Source.DataChild = me.tbsten.cream.generated.Source.DataChild(
    a = a,
)
````
