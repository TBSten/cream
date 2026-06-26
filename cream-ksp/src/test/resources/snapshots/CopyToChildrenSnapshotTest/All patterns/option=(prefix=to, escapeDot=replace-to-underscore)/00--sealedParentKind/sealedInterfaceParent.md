## Input:Input

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
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "under-package" /* default */)
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
 * Source -> Source.Child copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Source_Child(value = value)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Source_Child(value = value, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child
 */
public fun  me.tbsten.cream.generated.Source.to_Source_Child(
    value: Int,
) : me.tbsten.cream.generated.Source.Child = me.tbsten.cream.generated.Source.Child(
    value = value,
)
````
