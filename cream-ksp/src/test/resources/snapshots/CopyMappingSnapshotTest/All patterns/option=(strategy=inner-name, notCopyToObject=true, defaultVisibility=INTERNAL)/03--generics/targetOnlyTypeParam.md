## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyMapping

@CopyMapping(
  Source::class,
  Target::class,
)
public object Mapping

public data class Source(
  public val name: String,
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
// file: CopyMapping__Mapping.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
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
internal fun <T : Any?> me.tbsten.cream.generated.Source.copyToTarget(
    name: String = this.name,
    item: T,
) : me.tbsten.cream.generated.Target<T> = me.tbsten.cream.generated.Target(
    name = name,
    item = item,
)
````
