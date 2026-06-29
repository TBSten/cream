## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Comparable
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source<T : Comparable<T>> {
  public val item: T

  public data class Child<T : Comparable<T>>(
    override val item: T,
  ) : Source<T>
}
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
public fun <T : Comparable<T>> me.tbsten.cream.generated.Source<T>.to_Child(
    item: T = this.item,
) : me.tbsten.cream.generated.Source.Child<T> = me.tbsten.cream.generated.Source.Child(
    item = item,
)
````
