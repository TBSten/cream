## Input:me.tbsten.cream.generated.consume

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(
  ArgsA::class,
  ArgsB::class,
  funName = "of",
)
public fun consume(`value`: String) {
}

public data class ArgsA(
  public val `value`: String,
)

public data class ArgsB(
  public val `value`: String,
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
// file: CallFrom__consume.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [consume])
 * 
 * ArgsA -> consume() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val argsA = ArgsA(...)
 * of(argsA)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val argsA = ArgsA(...)
 * of(argsA, parameter = value)
 * ```
 * 
 * 
 * @see ArgsA
 */
public fun of(
    argsA: ArgsA,
    value: String = argsA.value,
): Unit = consume(
    value = value,
)

/**
 * (Auto generate by @[CallFrom] annotation of [consume])
 * 
 * ArgsB -> consume() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val argsB = ArgsB(...)
 * of(argsB)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val argsB = ArgsB(...)
 * of(argsB, parameter = value)
 * ```
 * 
 * 
 * @see ArgsB
 */
public fun of(
    argsB: ArgsB,
    value: String = argsB.value,
): Unit = consume(
    value = value,
)
````
