## Input:me.tbsten.cream.generated.Handler

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

public object Handler {
  @CallFrom(HandleArgs::class)
  public fun handle(`value`: String) {
  }
}

public data class HandleArgs(
  public val `value`: String,
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
// file: CallFrom__Handler_handle.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [Handler.handle])
 * 
 * HandleArgs -> Handler.handle() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val handleArgs = HandleArgs(...)
 * handler.handle(handleArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val handleArgs = HandleArgs(...)
 * handler.handle(handleArgs, parameter = value)
 * ```
 * 
 * 
 * @see HandleArgs
 */
public fun me.tbsten.cream.generated.Handler.handle(
    handleArgs: HandleArgs,
    value: String = handleArgs.value,
): Unit = handle(
    value = value,
)
````
