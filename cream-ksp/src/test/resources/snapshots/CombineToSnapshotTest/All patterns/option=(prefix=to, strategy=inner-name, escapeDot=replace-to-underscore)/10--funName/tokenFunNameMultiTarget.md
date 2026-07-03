## Input:me.tbsten.cream.generated.SourceA

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyTargetSimpleName

@CombineTo(
  TargetA::class,
  TargetB::class,
  funName = "to" + CopyTargetSimpleName,
)
public data class SourceA(
  public val name: String,
)

@CombineTo(
  TargetA::class,
  TargetB::class,
  funName = "to" + CopyTargetSimpleName,
)
public data class SourceB(
  public val extra: Int,
)

public data class TargetA(
  public val name: String,
  public val extra: Int,
)

public data class TargetB(
  public val name: String,
  public val extra: Int,
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
    arg("nonCopyableStrategy", "INHERIT" /* default */)
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
// file: CombineTo__SourceA__TargetA.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [SourceA])
 * 
 * [SourceA] + [SourceB] -> [TargetA] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.toTargetA(sourceB = SourceB(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.toTargetA(sourceB = SourceB(...), property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see TargetA
 */
public fun  me.tbsten.cream.generated.SourceA.toTargetA(
    sourceB: me.tbsten.cream.generated.SourceB,
    name: String = this.name,
    extra: Int = sourceB.extra,
) : me.tbsten.cream.generated.TargetA = me.tbsten.cream.generated.TargetA(
    name = name,
    extra = extra,
)

// ----- next file -----

// file: CombineTo__SourceA__TargetB.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [SourceA])
 * 
 * [SourceA] + [SourceB] -> [TargetB] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.toTargetB(sourceB = SourceB(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.toTargetB(sourceB = SourceB(...), property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see TargetB
 */
public fun  me.tbsten.cream.generated.SourceA.toTargetB(
    sourceB: me.tbsten.cream.generated.SourceB,
    name: String = this.name,
    extra: Int = sourceB.extra,
) : me.tbsten.cream.generated.TargetB = me.tbsten.cream.generated.TargetB(
    name = name,
    extra = extra,
)

// ----- next file -----

// file: CombineTo__SourceB__TargetA.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [SourceB])
 * 
 * [SourceB] + [SourceA] -> [TargetA] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.toTargetA(sourceA = SourceA(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.toTargetA(sourceA = SourceA(...), property = value)
 * ```
 * 
 * 
 * @see SourceB
 * @see SourceA
 * @see TargetA
 */
public fun  me.tbsten.cream.generated.SourceB.toTargetA(
    sourceA: me.tbsten.cream.generated.SourceA,
    name: String = sourceA.name,
    extra: Int = this.extra,
) : me.tbsten.cream.generated.TargetA = me.tbsten.cream.generated.TargetA(
    name = name,
    extra = extra,
)

// ----- next file -----

// file: CombineTo__SourceB__TargetB.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [SourceB])
 * 
 * [SourceB] + [SourceA] -> [TargetB] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.toTargetB(sourceA = SourceA(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.toTargetB(sourceA = SourceA(...), property = value)
 * ```
 * 
 * 
 * @see SourceB
 * @see SourceA
 * @see TargetB
 */
public fun  me.tbsten.cream.generated.SourceB.toTargetB(
    sourceA: me.tbsten.cream.generated.SourceA,
    name: String = sourceA.name,
    extra: Int = this.extra,
) : me.tbsten.cream.generated.TargetB = me.tbsten.cream.generated.TargetB(
    name = name,
    extra = extra,
)
````
