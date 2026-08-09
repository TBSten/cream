## Input:me.tbsten.cream.generated.log

```kt
package me.tbsten.cream.generated

import kotlin.Array
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(LogArgs::class)
public fun log(vararg items: String) {
}

public data class LogArgs(
  public val items: Array<String>,
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
// file: CallFrom__log.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [log])
 * 
 * LogArgs -> log() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val logArgs = LogArgs(...)
 * log(logArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val logArgs = LogArgs(...)
 * log(logArgs, parameter = value)
 * ```
 * 
 * 
 * @see LogArgs
 */
public fun log(
    logArgs: LogArgs,
    vararg items: String = logArgs.items,
): Unit = log(
    items = items,
)
````
