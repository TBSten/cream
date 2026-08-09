## Input:me.tbsten.cream.generated.pick

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.collections.List
import me.tbsten.cream.CallFrom

@CallFrom(PickArgs::class)
public fun <T> List<T>.pick(index: Int): T = this[index]

public data class PickArgs(
  public val index: Int,
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
// file: CallFrom__pick.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [pick])
 * 
 * PickArgs -> pick() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val pickArgs = PickArgs(...)
 * list.pick(pickArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val pickArgs = PickArgs(...)
 * list.pick(pickArgs, parameter = value)
 * ```
 * 
 * 
 * @see PickArgs
 */
public fun <T : Any?> kotlin.collections.List<T>.pick(
    pickArgs: PickArgs,
    index: Int = pickArgs.index,
): T = pick(
    index = index,
)
````
