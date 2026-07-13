## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  public data class Child(
    public val `value`: Int,
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
 * Source -> Source.Child copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToChild(value = value)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToChild(value = value, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child
 */
internal fun  me.tbsten.cream.generated.Source.copyToChild(
    value: Int,
) : me.tbsten.cream.generated.Source.Child = me.tbsten.cream.generated.Source.Child(
    value = value,
)
````
