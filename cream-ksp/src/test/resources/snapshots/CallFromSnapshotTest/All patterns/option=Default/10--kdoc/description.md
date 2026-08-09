## Input:me.tbsten.cream.generated.notify

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom
import me.tbsten.cream.KDoc

@CallFrom(
  NotifyArgs::class,
  kdoc = KDoc(description = "Custom description for the bridge function."),
)
public fun notify(message: String) {
}

public data class NotifyArgs(
  public val message: String,
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
// file: CallFrom__notify.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [notify])
 * 
 * NotifyArgs -> notify() bridge function.
 * 
 * Custom description for the bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val notifyArgs = NotifyArgs(...)
 * notify(notifyArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val notifyArgs = NotifyArgs(...)
 * notify(notifyArgs, parameter = value)
 * ```
 * 
 * 
 * @see NotifyArgs
 */
public fun notify(
    notifyArgs: NotifyArgs,
    message: String = notifyArgs.message,
): Unit = notify(
    message = message,
)
````
