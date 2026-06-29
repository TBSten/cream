## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Comparable
import me.tbsten.cream.CombineFrom

@CombineFrom(Source::class)
public data class Target<T : Comparable<T>>(
  public val item: T,
)

public data class Source<T : Comparable<T>>(
  public val item: T,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
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
// file: CombineFrom__Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [Target])
 * 
 * [Source] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
internal fun <T : Comparable<T>> me.tbsten.cream.generated.Source<T>.copyToTarget(
    item: T = this.item,
) : me.tbsten.cream.generated.Target<T> = me.tbsten.cream.generated.Target(
    item = item,
)
````
