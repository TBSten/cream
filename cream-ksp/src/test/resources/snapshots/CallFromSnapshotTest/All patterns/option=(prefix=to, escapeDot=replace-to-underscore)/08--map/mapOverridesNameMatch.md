## Input:me.tbsten.cream.generated.send

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(SendArgs::class)
public fun send(@CallFrom.Map("beta") alpha: String) {
}

public data class SendArgs(
  public val alpha: String,
  public val beta: String,
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
// file: CallFrom__send.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [send])
 * 
 * SendArgs -> send() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sendArgs = SendArgs(...)
 * send(sendArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val sendArgs = SendArgs(...)
 * send(sendArgs, parameter = value)
 * ```
 * 
 * 
 * @see SendArgs
 */
public fun send(
    sendArgs: SendArgs,
    alpha: String = sendArgs.beta,
): Unit = send(
    alpha = alpha,
)
````
