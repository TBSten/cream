## Input:me.tbsten.cream.generated.fail

```kt
package me.tbsten.cream.generated

import kotlin.Nothing
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(FailArgs::class)
public fun fail(message: String): Nothing = throw IllegalStateException(message)

public data class FailArgs(
  public val message: String,
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
// file: CallFrom__fail.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [fail])
 * 
 * FailArgs -> fail() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val failArgs = FailArgs(...)
 * fail(failArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val failArgs = FailArgs(...)
 * fail(failArgs, parameter = value)
 * ```
 * 
 * 
 * @see FailArgs
 */
public fun fail(
    failArgs: FailArgs,
    message: String = failArgs.message,
): Nothing = fail(
    message = message,
)
````
