## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren(
  notCopyToObject = true,
  funName = "toState",
)
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
 * Source -> Source.DataChild copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toState(a = a)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toState(a = a, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.DataChild
 */
internal fun  me.tbsten.cream.generated.Source.toState(
    a: String,
) : me.tbsten.cream.generated.Source.DataChild = me.tbsten.cream.generated.Source.DataChild(
    a = a,
)
````
