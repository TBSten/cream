## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineMapping

@CombineMapping(
  sources = [LibA::class, LibB::class],
  target = Target::class,
)
@CombineMapping(
  sources = [LibB::class, LibC::class],
  target = Target::class,
)
public object Mapping

public data class LibA(
  public val shared: String,
  public val aProp: Int,
)

public data class LibB(
  public val shared: String,
  public val bProp: Int,
)

public data class LibC(
  public val shared: String,
  public val cProp: Int,
)

public data class Target(
  public val shared: String,
  public val aProp: Int,
  public val bProp: Int,
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
// file: CombineMapping__Mapping.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineMapping] annotation of [Mapping])
 * 
 * [LibA] + [LibB] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val libA = LibA(...)
 * val libB = LibB(...)
 * val target = libA.copyToTarget(libB = LibB(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val libA = LibA(...)
 * val libB = LibB(...)
 * val target = libA.copyToTarget(libB = LibB(...), property = value)
 * ```
 * 
 * 
 * @see LibA
 * @see LibB
 * @see Target
 */
internal fun  me.tbsten.cream.generated.LibA.copyToTarget(
    libB: me.tbsten.cream.generated.LibB,
    shared: String = libB.shared,
    aProp: Int = this.aProp,
    bProp: Int = libB.bProp,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    shared = shared,
    aProp = aProp,
    bProp = bProp,
)

/**
 * (Auto generate by @[CombineMapping] annotation of [Mapping])
 * 
 * [LibB] + [LibC] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val libB = LibB(...)
 * val libC = LibC(...)
 * val target = libB.copyToTarget(libC = LibC(...), aProp = aProp)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val libB = LibB(...)
 * val libC = LibC(...)
 * val target = libB.copyToTarget(libC = LibC(...), aProp = aProp, property = value)
 * ```
 * 
 * 
 * @see LibB
 * @see LibC
 * @see Target
 */
internal fun  me.tbsten.cream.generated.LibB.copyToTarget(
    libC: me.tbsten.cream.generated.LibC,
    shared: String = libC.shared,
    aProp: Int,
    bProp: Int = this.bProp,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    shared = shared,
    aProp = aProp,
    bProp = bProp,
)
````
