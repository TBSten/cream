## Input:Input

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
public fun <T : Comparable<T>> me.tbsten.cream.generated.Source<T>.copyToSourceChild(
    item: T = this.item,
) : me.tbsten.cream.generated.Source.Child<T> = me.tbsten.cream.generated.Source.Child(
    item = item,
)
````
