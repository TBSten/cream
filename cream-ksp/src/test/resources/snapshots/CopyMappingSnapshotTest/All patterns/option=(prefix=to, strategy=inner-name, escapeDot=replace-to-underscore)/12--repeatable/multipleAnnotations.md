## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyMapping

@CopyMapping(
  LibA::class,
  LibB::class,
)
@CopyMapping(
  LibB::class,
  LibC::class,
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
// file: CopyMapping__Mapping.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * LibA -> LibB copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = LibA(...)
 * val target = source.to_LibB(bProp = bProp)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = LibA(...)
 * val target = source.to_LibB(bProp = bProp, property = value)
 * ```
 * 
 * 
 * @see LibA
 * @see LibB
 */
public fun  me.tbsten.cream.generated.LibA.to_LibB(
    shared: String = this.shared,
    bProp: Int,
) : me.tbsten.cream.generated.LibB = me.tbsten.cream.generated.LibB(
    shared = shared,
    bProp = bProp,
)

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * LibB -> LibC copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = LibB(...)
 * val target = source.to_LibC(cProp = cProp)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = LibB(...)
 * val target = source.to_LibC(cProp = cProp, property = value)
 * ```
 * 
 * 
 * @see LibB
 * @see LibC
 */
public fun  me.tbsten.cream.generated.LibB.to_LibC(
    shared: String = this.shared,
    cProp: Int,
) : me.tbsten.cream.generated.LibC = me.tbsten.cream.generated.LibC(
    shared = shared,
    cProp = cProp,
)
````
