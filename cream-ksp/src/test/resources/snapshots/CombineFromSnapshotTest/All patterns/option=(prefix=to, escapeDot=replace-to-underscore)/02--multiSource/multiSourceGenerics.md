## Input:Input

```kt
package me.tbsten.cream.generated

import me.tbsten.cream.CombineFrom

@CombineFrom(
  SourceA::class,
  SourceB::class,
)
public data class Target<T, U>(
  public val a: T,
  public val b: U,
)

public data class SourceA<T>(
  public val a: T,
)

public data class SourceB<U>(
  public val b: U,
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
public fun <T : Any?, U : Any?> me.tbsten.cream.generated.SourceA<T>.to_Target(
    sourceB: me.tbsten.cream.generated.SourceB<U>,
    a: T = this.a,
    b: U = sourceB.b,
) : me.tbsten.cream.generated.Target<T, U> = me.tbsten.cream.generated.Target(
    a = a,
    b = b,
)
````
