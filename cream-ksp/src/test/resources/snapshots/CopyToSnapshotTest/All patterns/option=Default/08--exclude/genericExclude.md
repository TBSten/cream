## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
public data class Source<T>(
  public val name: String,
  @CopyTo.Exclude
  public val item: T,
)

public data class Target<T>(
  public val name: String,
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
 * val target = source.copyToTarget(item = item)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(item = item, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun <T : Any?> me.tbsten.cream.generated.Source<T>.copyToTarget(
    name: String = this.name,
    item: T,
) : me.tbsten.cream.generated.Target<T> = me.tbsten.cream.generated.Target(
    name = name,
    item = item,
)
````
