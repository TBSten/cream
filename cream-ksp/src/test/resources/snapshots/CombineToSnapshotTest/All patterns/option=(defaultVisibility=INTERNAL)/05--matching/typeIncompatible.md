## Input:me.tbsten.cream.generated.SourceA

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineTo

@CombineTo(Target::class)
public data class SourceA(
  public val `value`: String,
)

@CombineTo(Target::class)
public data class SourceB(
  public val extra: Int,
)

public data class Target(
  public val `value`: Int,
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
 * # Example: Basic
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.copyToTarget(sourceB = SourceB(...), value = value)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.copyToTarget(sourceB = SourceB(...), value = value, property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see Target
 */
internal fun  me.tbsten.cream.generated.SourceA.copyToTarget(
    sourceB: me.tbsten.cream.generated.SourceB,
    value: Int,
    extra: Int = sourceB.extra,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    value = value,
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
 * # Example: Basic
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.copyToTarget(sourceA = SourceA(...), value = value)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.copyToTarget(sourceA = SourceA(...), value = value, property = value)
 * ```
 * 
 * 
 * @see SourceB
 * @see SourceA
 * @see Target
 */
internal fun  me.tbsten.cream.generated.SourceB.copyToTarget(
    sourceA: me.tbsten.cream.generated.SourceA,
    value: Int,
    extra: Int = this.extra,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    value = value,
    extra = extra,
)
````
