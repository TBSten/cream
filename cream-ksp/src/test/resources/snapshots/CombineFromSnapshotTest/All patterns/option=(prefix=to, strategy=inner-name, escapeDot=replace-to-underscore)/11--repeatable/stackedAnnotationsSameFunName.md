## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineFrom

@CombineFrom(
  SourceA::class,
  funName = "toTarget",
)
@CombineFrom(
  SourceB::class,
  funName = "toTarget",
)
public data class Target(
  public val propertyA: String,
  public val propertyB: Int,
)

public data class SourceA(
  public val propertyA: String,
)

public data class SourceB(
  public val propertyB: Int,
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
 * [SourceA] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val target = sourceA.toTarget(propertyB = propertyB)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val target = sourceA.toTarget(propertyB = propertyB, property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceA.toTarget(
    propertyA: String = this.propertyA,
    propertyB: Int,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    propertyA = propertyA,
    propertyB = propertyB,
)

/**
 * (Auto generate by @[CombineFrom] annotation of [Target])
 * 
 * [SourceB] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val target = sourceB.toTarget(propertyA = propertyA)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val target = sourceB.toTarget(propertyA = propertyA, property = value)
 * ```
 * 
 * 
 * @see SourceB
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceB.toTarget(
    propertyA: String,
    propertyB: Int = this.propertyB,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    propertyA = propertyA,
    propertyB = propertyB,
)
````
