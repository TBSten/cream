## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineMapping

@CombineMapping(
  sources = [SourceA::class, SourceB::class, SourceC::class],
  target = Target::class,
)
public object Mapping

public data class SourceA(
  public val propertyA: String,
)

public data class SourceB(
  public val propertyB: Int,
)

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
// file: CombineMapping__Mapping.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineMapping] annotation of [Mapping])
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
````
