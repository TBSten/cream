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
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
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
 * val target = source.copyToLibB(bProp = bProp)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = LibA(...)
 * val target = source.copyToLibB(bProp = bProp, property = value)
 * ```
 * 
 * 
 * @see LibA
 * @see LibB
 */
public fun  me.tbsten.cream.generated.LibA.copyToLibB(
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
 * val target = source.copyToLibC(cProp = cProp)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = LibB(...)
 * val target = source.copyToLibC(cProp = cProp, property = value)
 * ```
 * 
 * 
 * @see LibB
 * @see LibC
 */
public fun  me.tbsten.cream.generated.LibB.copyToLibC(
    shared: String = this.shared,
    cProp: Int,
) : me.tbsten.cream.generated.LibC = me.tbsten.cream.generated.LibC(
    shared = shared,
    cProp = cProp,
)
````
