## Input:me.tbsten.cream.generated.SourceA

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineTo
import me.tbsten.cream.KDoc

@CombineTo(
  Target::class,
  kdoc = KDoc(description = "Use this only when migrating.", examples = ["# Recommended\n\nval target = source.copyToTarget()"]),
)
public data class SourceA(
  public val name: String,
)

@CombineTo(
  Target::class,
  kdoc = KDoc(description = "Use this only when migrating.", examples = ["# Recommended\n\nval target = source.copyToTarget()"]),
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
// file: CombineTo__SourceA__Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [SourceA])
 * 
 * [SourceA] + [SourceB] -> [Target] copy function.
 * 
 * Use this only when migrating.
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
 * # Recommended
 * 
 * val target = source.copyToTarget()
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see Target
 */
internal fun  me.tbsten.cream.generated.SourceA.copyToTarget(
    sourceB: me.tbsten.cream.generated.SourceB,
    name: String = this.name,
    extra: Int = sourceB.extra,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    name = name,
    extra = extra,
)

// ----- next file -----

// file: CombineTo__SourceB__Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [SourceB])
 * 
 * [SourceB] + [SourceA] -> [Target] copy function.
 * 
 * Use this only when migrating.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.copyToTarget(sourceA = SourceA(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.copyToTarget(sourceA = SourceA(...), property = value)
 * ```
 * 
 * # Recommended
 * 
 * val target = source.copyToTarget()
 * 
 * 
 * @see SourceB
 * @see SourceA
 * @see Target
 */
internal fun  me.tbsten.cream.generated.SourceB.copyToTarget(
    sourceA: me.tbsten.cream.generated.SourceA,
    name: String = sourceA.name,
    extra: Int = this.extra,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    name = name,
    extra = extra,
)
````
