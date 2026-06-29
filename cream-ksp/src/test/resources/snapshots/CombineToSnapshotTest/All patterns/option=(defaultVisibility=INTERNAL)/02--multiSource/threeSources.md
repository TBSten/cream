## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineTo

@CombineTo(Target::class)
public data class SourceA(
  public val propertyA: String,
)

@CombineTo(Target::class)
public data class SourceB(
  public val propertyB: Int,
)

@CombineTo(Target::class)
public data class SourceC(
  public val propertyC: Boolean,
)

public data class Target(
  public val propertyA: String,
  public val propertyB: Int,
  public val propertyC: Boolean,
  public val propertyD: Double,
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
 * [SourceA] + [SourceB] + [SourceC] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val sourceC = SourceC(...)
 * val target = sourceA.copyToTarget(sourceB = SourceB(...), sourceC = SourceC(...), propertyD = propertyD)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val sourceC = SourceC(...)
 * val target = sourceA.copyToTarget(sourceB = SourceB(...), sourceC = SourceC(...), propertyD = propertyD, property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see SourceC
 * @see Target
 */
internal fun  me.tbsten.cream.generated.SourceA.copyToTarget(
    sourceB: me.tbsten.cream.generated.SourceB,
    sourceC: me.tbsten.cream.generated.SourceC,
    propertyA: String = this.propertyA,
    propertyB: Int = sourceB.propertyB,
    propertyC: Boolean = sourceC.propertyC,
    propertyD: Double,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    propertyA = propertyA,
    propertyB = propertyB,
    propertyC = propertyC,
    propertyD = propertyD,
)

// ----- next file -----

// file: CombineTo__SourceB__Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [SourceB])
 * 
 * [SourceB] + [SourceA] + [SourceC] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val sourceC = SourceC(...)
 * val target = sourceB.copyToTarget(sourceA = SourceA(...), sourceC = SourceC(...), propertyD = propertyD)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val sourceC = SourceC(...)
 * val target = sourceB.copyToTarget(sourceA = SourceA(...), sourceC = SourceC(...), propertyD = propertyD, property = value)
 * ```
 * 
 * 
 * @see SourceB
 * @see SourceA
 * @see SourceC
 * @see Target
 */
internal fun  me.tbsten.cream.generated.SourceB.copyToTarget(
    sourceA: me.tbsten.cream.generated.SourceA,
    sourceC: me.tbsten.cream.generated.SourceC,
    propertyA: String = sourceA.propertyA,
    propertyB: Int = this.propertyB,
    propertyC: Boolean = sourceC.propertyC,
    propertyD: Double,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    propertyA = propertyA,
    propertyB = propertyB,
    propertyC = propertyC,
    propertyD = propertyD,
)

// ----- next file -----

// file: CombineTo__SourceC__Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [SourceC])
 * 
 * [SourceC] + [SourceA] + [SourceB] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceC = SourceC(...)
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceC.copyToTarget(sourceA = SourceA(...), sourceB = SourceB(...), propertyD = propertyD)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceC = SourceC(...)
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceC.copyToTarget(sourceA = SourceA(...), sourceB = SourceB(...), propertyD = propertyD, property = value)
 * ```
 * 
 * 
 * @see SourceC
 * @see SourceA
 * @see SourceB
 * @see Target
 */
internal fun  me.tbsten.cream.generated.SourceC.copyToTarget(
    sourceA: me.tbsten.cream.generated.SourceA,
    sourceB: me.tbsten.cream.generated.SourceB,
    propertyA: String = sourceA.propertyA,
    propertyB: Int = sourceB.propertyB,
    propertyC: Boolean = this.propertyC,
    propertyD: Double,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    propertyA = propertyA,
    propertyB = propertyB,
    propertyC = propertyC,
    propertyD = propertyD,
)
````
