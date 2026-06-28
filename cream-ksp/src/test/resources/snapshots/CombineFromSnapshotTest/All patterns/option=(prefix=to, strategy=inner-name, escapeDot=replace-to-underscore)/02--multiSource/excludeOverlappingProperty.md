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
  @CombineFrom.Exclude
  public val shared: String,
  public val uniqueA: Int,
  public val uniqueB: Boolean,
)

public data class SourceA(
  public val shared: String,
  public val uniqueA: Int,
)

public data class SourceB(
  public val shared: String,
  public val uniqueB: Boolean,
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
 * val target = sourceA.to_Target(sourceB = SourceB(...), shared = shared)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.to_Target(sourceB = SourceB(...), shared = shared, property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceA.to_Target(
    sourceB: me.tbsten.cream.generated.SourceB,
    shared: String,
    uniqueA: Int = this.uniqueA,
    uniqueB: Boolean = sourceB.uniqueB,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    shared = shared,
    uniqueA = uniqueA,
    uniqueB = uniqueB,
)
````
