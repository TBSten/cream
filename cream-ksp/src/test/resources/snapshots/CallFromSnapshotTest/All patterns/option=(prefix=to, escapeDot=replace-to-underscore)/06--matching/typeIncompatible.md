## Input:me.tbsten.cream.generated.resize

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(ResizeArgs::class)
public fun resize(`value`: Int) {
}

public data class ResizeArgs(
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
// file: CallFrom__resize.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [resize])
 * 
 * ResizeArgs -> resize() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val resizeArgs = ResizeArgs(...)
 * resize(resizeArgs, value = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val resizeArgs = ResizeArgs(...)
 * resize(resizeArgs, value = ..., parameter = value)
 * ```
 * 
 * 
 * @see ResizeArgs
 */
public fun resize(
    resizeArgs: ResizeArgs,
    value: Int,
): Unit = resize(
    value = value,
)
````
