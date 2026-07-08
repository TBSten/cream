## Input:me.tbsten.cream.generated.notify

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom
import me.tbsten.cream.KDoc

@CallFrom(
  NotifyArgs::class,
  kdoc = KDoc(description = "Use this only when migrating.", examples = ["# Recommended\n\nnotify(NotifyArgs(\"hi\"))"]),
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
// file: CallFrom__notify.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [notify])
 * 
 * NotifyArgs -> notify() bridge function.
 * 
 * Use this only when migrating.
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
 * # Recommended
 * 
 * notify(NotifyArgs("hi"))
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
