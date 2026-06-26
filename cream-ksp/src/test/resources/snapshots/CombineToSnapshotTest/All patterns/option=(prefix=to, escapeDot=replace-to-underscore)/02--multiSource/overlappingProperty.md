## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineTo

@CombineTo(Target::class)
public data class SourceA(
  public val shared: String,
  public val uniqueA: Int,
)

@CombineTo(Target::class)
public data class SourceB(
  public val shared: String,
  public val uniqueB: Boolean,
)

public data class Target(
  public val shared: String,
  public val uniqueA: Int,
  public val uniqueB: Boolean,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "under-package" /* default */)
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
 * val target = sourceA.to_Target(sourceB = SourceB(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.to_Target(sourceB = SourceB(...), property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceA.to_Target(
    sourceB: me.tbsten.cream.generated.SourceB,
    shared: String = sourceB.shared,
    uniqueA: Int = this.uniqueA,
    uniqueB: Boolean = sourceB.uniqueB,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    shared = shared,
    uniqueA = uniqueA,
    uniqueB = uniqueB,
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
 * val target = sourceB.to_Target(sourceA = SourceA(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.to_Target(sourceA = SourceA(...), property = value)
 * ```
 * 
 * 
 * @see SourceB
 * @see SourceA
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceB.to_Target(
    sourceA: me.tbsten.cream.generated.SourceA,
    shared: String = sourceA.shared,
    uniqueA: Int = sourceA.uniqueA,
    uniqueB: Boolean = this.uniqueB,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    shared = shared,
    uniqueA = uniqueA,
    uniqueB = uniqueB,
)
````
