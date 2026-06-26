## Input:Input

```kt
package me.tbsten.cream.generated

import me.tbsten.cream.CombineTo

@CombineTo(Target::class)
public data class SourceA<T>(
  public val a: T,
)

@CombineTo(Target::class)
public data class SourceB<U>(
  public val b: U,
)

public data class Target<T, U>(
  public val a: T,
  public val b: U,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
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
 * val target = sourceA.copyToTarget(sourceB = SourceB(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val sourceB = SourceB(...)
 * val target = sourceA.copyToTarget(sourceB = SourceB(...), property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see SourceB
 * @see Target
 */
public fun <T : Any?, U : Any?> me.tbsten.cream.generated.SourceA<T>.copyToTarget(
    sourceB: me.tbsten.cream.generated.SourceB<U>,
    a: T = this.a,
    b: U = sourceB.b,
) : me.tbsten.cream.generated.Target<T, U> = me.tbsten.cream.generated.Target(
    a = a,
    b = b,
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
 * val target = sourceB.copyToTarget(sourceA = SourceA(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceB = SourceB(...)
 * val sourceA = SourceA(...)
 * val target = sourceB.copyToTarget(sourceA = SourceA(...), property = value)
 * ```
 * 
 * 
 * @see SourceB
 * @see SourceA
 * @see Target
 */
public fun <T : Any?, U : Any?> me.tbsten.cream.generated.SourceB<U>.copyToTarget(
    sourceA: me.tbsten.cream.generated.SourceA<T>,
    a: T = sourceA.a,
    b: U = this.b,
) : me.tbsten.cream.generated.Target<T, U> = me.tbsten.cream.generated.Target(
    a = a,
    b = b,
)
````
