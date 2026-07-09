## Input:me.tbsten.cream.generated.orValue

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(OrValueArgs::class)
public suspend fun String?.orValue(fallback: String): String = this ?: fallback

public data class OrValueArgs(
  public val fallback: String,
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
// file: CallFrom__orValue.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [orValue])
 * 
 * OrValueArgs -> orValue() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val orValueArgs = OrValueArgs(...)
 * string.orValue(orValueArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val orValueArgs = OrValueArgs(...)
 * string.orValue(orValueArgs, parameter = value)
 * ```
 * 
 * 
 * @see OrValueArgs
 */
internal suspend fun String?.orValue(
    orValueArgs: OrValueArgs,
    fallback: String = orValueArgs.fallback,
): String = orValue(
    fallback = fallback,
)
````
