## Input:me.tbsten.cream.generated.submit

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(SubmitArgs::class)
public fun submit(id: String, comment: String) {
}

public data class SubmitArgs(
  public val id: String,
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
// file: CallFrom__submit.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [submit])
 * 
 * SubmitArgs -> submit() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val submitArgs = SubmitArgs(...)
 * submit(submitArgs, comment = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val submitArgs = SubmitArgs(...)
 * submit(submitArgs, comment = ..., parameter = value)
 * ```
 * 
 * 
 * @see SubmitArgs
 */
public fun submit(
    submitArgs: SubmitArgs,
    id: String = submitArgs.id,
    comment: String,
): Unit = submit(
    id = id,
    comment = comment,
)
````
