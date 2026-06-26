## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineFrom

@CombineFrom(
  SourceA::class,
  SourceB::class,
)
public data class Target(
  public val propertyA: String,
  public val propertyB: Int,
  public val propertyC: Boolean,
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
// file: CombineFrom__SourceA__Target.kt
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
 * val target = sourceA.to_Target(sourceB = SourceB(...), propertyC = propertyC)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.to_Target(sourceB = SourceB(...), propertyC = propertyC, property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceA.to_Target(
    sourceB: me.tbsten.cream.generated.SourceB,
    propertyA: String = this.propertyA,
    propertyB: Int = sourceB.propertyB,
    propertyC: Boolean,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    propertyA = propertyA,
    propertyB = propertyB,
    propertyC = propertyC,
)
````
