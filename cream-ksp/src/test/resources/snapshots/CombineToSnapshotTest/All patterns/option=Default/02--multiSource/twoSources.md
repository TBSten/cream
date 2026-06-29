## Input:me.tbsten.cream.generated.SourceA

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
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

public data class Target(
  public val propertyA: String,
  public val propertyB: Int,
  public val propertyC: Boolean,
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
 * val target = sourceA.copyToTarget(sourceB = SourceB(...), propertyC = propertyC)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.copyToTarget(sourceB = SourceB(...), propertyC = propertyC, property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceA.copyToTarget(
    sourceB: me.tbsten.cream.generated.SourceB,
    propertyA: String = this.propertyA,
    propertyB: Int = sourceB.propertyB,
    propertyC: Boolean,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    propertyA = propertyA,
    propertyB = propertyB,
    propertyC = propertyC,
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
 * val target = sourceB.copyToTarget(sourceA = SourceA(...), propertyC = propertyC)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.copyToTarget(sourceA = SourceA(...), propertyC = propertyC, property = value)
 * ```
 * 
 * 
 * @see SourceB
 * @see SourceA
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceB.copyToTarget(
    sourceA: me.tbsten.cream.generated.SourceA,
    propertyA: String = sourceA.propertyA,
    propertyB: Int = this.propertyB,
    propertyC: Boolean,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    propertyA = propertyA,
    propertyB = propertyB,
    propertyC = propertyC,
)
````
