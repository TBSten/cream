## Input:me.tbsten.cream.generated.Counter

```kt
package me.tbsten.cream.generated

import kotlin.Int
import me.tbsten.cream.CallFrom

public class Counter(
  public val `value`: Int,
) {
  @CallFrom(AddArgs::class)
  public operator fun plus(other: Int): Counter = Counter(value + other)
}

public data class AddArgs(
  public val other: Int,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "under-package" /* default */)
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
// file: CallFrom__Counter_plus.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [Counter.plus])
 * 
 * AddArgs -> Counter.plus() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val addArgs = AddArgs(...)
 * counter.plus(addArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val addArgs = AddArgs(...)
 * counter.plus(addArgs, parameter = value)
 * ```
 * 
 * 
 * @see AddArgs
 */
public fun me.tbsten.cream.generated.Counter.plus(
    addArgs: AddArgs,
    other: Int = addArgs.other,
): Counter = plus(
    other = other,
)
````
