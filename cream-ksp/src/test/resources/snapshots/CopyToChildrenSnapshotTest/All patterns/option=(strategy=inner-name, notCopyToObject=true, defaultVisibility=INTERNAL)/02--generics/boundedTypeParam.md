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
 * val target = source.copyToChild()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToChild(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child
 */
internal fun <T : Comparable<T>> me.tbsten.cream.generated.Source<T>.copyToChild(
    item: T = this.item,
) : me.tbsten.cream.generated.Source.Child<T> = me.tbsten.cream.generated.Source.Child(
    item = item,
)
````
