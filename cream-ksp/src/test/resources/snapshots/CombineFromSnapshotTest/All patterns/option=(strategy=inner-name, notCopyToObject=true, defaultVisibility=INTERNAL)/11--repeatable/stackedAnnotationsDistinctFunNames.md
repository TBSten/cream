## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineFrom

@CombineFrom(
  SourceA::class,
  SourceB::class,
  funName = "toFoo",
)
@CombineFrom(
  SourceC::class,
  SourceD::class,
  funName = "toBar",
)
public data class Target(
  public val propertyA: String,
  public val propertyB: Int,
  public val propertyC: String,
  public val propertyD: Int,
)

public data class SourceA(
  public val propertyA: String,
)

public data class SourceB(
  public val propertyB: Int,
)

public data class SourceC(
  public val propertyC: String,
)

public data class SourceD(
  public val propertyD: Int,
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
// file: CombineFrom__Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [Target])
 * 
 * [SourceA] + [SourceB] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.toFoo(sourceB = SourceB(...), propertyC = propertyC, propertyD = propertyD)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.toFoo(sourceB = SourceB(...), propertyC = propertyC, propertyD = propertyD, property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see Target
 */
internal fun  me.tbsten.cream.generated.SourceA.toFoo(
    sourceB: me.tbsten.cream.generated.SourceB,
    propertyA: String = this.propertyA,
    propertyB: Int = sourceB.propertyB,
    propertyC: String,
    propertyD: Int,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    propertyA = propertyA,
    propertyB = propertyB,
    propertyC = propertyC,
    propertyD = propertyD,
)

/**
 * (Auto generate by @[CombineFrom] annotation of [Target])
 * 
 * [SourceC] + [SourceD] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceC = SourceC(...)
 * val sourceD = SourceD(...)
 * val target = sourceC.toBar(sourceD = SourceD(...), propertyA = propertyA, propertyB = propertyB)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceC = SourceC(...)
 * val sourceD = SourceD(...)
 * val target = sourceC.toBar(sourceD = SourceD(...), propertyA = propertyA, propertyB = propertyB, property = value)
 * ```
 * 
 * 
 * @see SourceC
 * @see SourceD
 * @see Target
 */
internal fun  me.tbsten.cream.generated.SourceC.toBar(
    sourceD: me.tbsten.cream.generated.SourceD,
    propertyA: String,
    propertyB: Int,
    propertyC: String = this.propertyC,
    propertyD: Int = sourceD.propertyD,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    propertyA = propertyA,
    propertyB = propertyB,
    propertyC = propertyC,
    propertyD = propertyD,
)
````
