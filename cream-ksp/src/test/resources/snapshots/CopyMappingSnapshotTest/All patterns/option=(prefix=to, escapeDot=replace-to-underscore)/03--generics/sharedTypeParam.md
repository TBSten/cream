## Input:Input

```kt
package me.tbsten.cream.generated

import me.tbsten.cream.CopyMapping

@CopyMapping(
  Source::class,
  Target::class,
)
public object Mapping

public data class Source<T>(
  public val item: T,
)

public data class Target<T>(
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
public fun <T : Any?> me.tbsten.cream.generated.Source<T>.to_Target(
    item: T = this.item,
) : me.tbsten.cream.generated.Target<T> = me.tbsten.cream.generated.Target(
    item = item,
)
````
