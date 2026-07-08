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
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INTERNAL")
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
internal fun submit(
    submitArgs: SubmitArgs,
    id: String = submitArgs.id,
    comment: String,
): Unit = submit(
    id = id,
    comment = comment,
)
````
