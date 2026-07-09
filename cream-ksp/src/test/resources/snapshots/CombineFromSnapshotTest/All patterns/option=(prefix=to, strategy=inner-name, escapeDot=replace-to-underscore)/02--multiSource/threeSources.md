## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineFrom

@CombineFrom(
  SourceA::class,
  SourceB::class,
  SourceC::class,
)
public data class Target(
  public val propertyA: String,
  public val propertyB: Int,
  public val propertyC: Boolean,
  public val propertyD: Double,
)

public data class SourceA(
  public val propertyA: String,
)

public data class SourceB(
  public val propertyB: Int,
)

public data class SourceC(
  public val propertyC: Boolean,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "inner-name")
    arg("escapeDot", "replace-to-underscore")
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INHERIT" /* default */)
    arg("autoValueClassMapping", "true" /* default */)
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
 * [SourceA] + [SourceB] + [SourceC] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val sourceC = SourceC(...)
 * val target = sourceA.to_Target(sourceB = SourceB(...), sourceC = SourceC(...), propertyD = propertyD)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val sourceC = SourceC(...)
 * val target = sourceA.to_Target(sourceB = SourceB(...), sourceC = SourceC(...), propertyD = propertyD, property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see SourceC
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceA.to_Target(
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
````
