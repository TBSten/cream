## Input:me.tbsten.cream.generated.runTagged

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(RunArgs::class)
public inline fun runTagged(tag: String, block: () -> String): String = tag + block()

public data class RunArgs(
  public val tag: String,
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
// file: CallFrom__runTagged.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [runTagged])
 * 
 * RunArgs -> runTagged() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val runArgs = RunArgs(...)
 * runTagged(runArgs, block = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val runArgs = RunArgs(...)
 * runTagged(runArgs, block = ..., parameter = value)
 * ```
 * 
 * 
 * @see RunArgs
 */
public fun runTagged(
    runArgs: RunArgs,
    tag: String = runArgs.tag,
    block: Function0<String>,
): String = runTagged(
    tag = tag,
    block = block,
)
````
