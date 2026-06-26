## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Comparable
import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
public data class Source<T : Comparable<T>>(
  public val item: T,
)

public data class Target<T : Comparable<T>>(
  public val item: T,
)
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
// file: CopyTo__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Target()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Target(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun <T : Comparable<T>> me.tbsten.cream.generated.Source<T>.to_Target(
    item: T = this.item,
) : me.tbsten.cream.generated.Target<T> = me.tbsten.cream.generated.Target(
    item = item,
)
````
