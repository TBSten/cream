## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineMapping
import me.tbsten.cream.KDoc

@CombineMapping(
  sources = [SourceA::class, SourceB::class],
  target = Target::class,
  kdoc = KDoc(description = "Custom description for the combine function."),
)
public object Mapping

public data class SourceA(
  public val name: String,
)

public data class SourceB(
  public val extra: Int,
)

public data class Target(
  public val name: String,
  public val extra: Int,
)
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
// file: CombineMapping__Mapping.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineMapping] annotation of [Mapping])
 * 
 * [SourceA] + [SourceB] -> [Target] copy function.
 * 
 * Custom description for the combine function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.copyToTarget(sourceB = SourceB(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.copyToTarget(sourceB = SourceB(...), property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceA.copyToTarget(
    sourceB: me.tbsten.cream.generated.SourceB,
    name: String = this.name,
    extra: Int = sourceB.extra,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    name = name,
    extra = extra,
)
````
