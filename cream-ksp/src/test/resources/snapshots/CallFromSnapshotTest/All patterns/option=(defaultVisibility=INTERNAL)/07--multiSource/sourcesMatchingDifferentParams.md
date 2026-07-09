## Input:me.tbsten.cream.generated.update

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(
  IdArgs::class,
  CountArgs::class,
)
public fun update(id: String, count: Int) {
}

public data class IdArgs(
  public val id: String,
)

public data class CountArgs(
  public val count: Int,
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
// file: CallFrom__update.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [update])
 * 
 * IdArgs -> update() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val idArgs = IdArgs(...)
 * update(idArgs, count = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val idArgs = IdArgs(...)
 * update(idArgs, count = ..., parameter = value)
 * ```
 * 
 * 
 * @see IdArgs
 */
internal fun update(
    idArgs: IdArgs,
    id: String = idArgs.id,
    count: Int,
): Unit = update(
    id = id,
    count = count,
)

/**
 * (Auto generate by @[CallFrom] annotation of [update])
 * 
 * CountArgs -> update() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val countArgs = CountArgs(...)
 * update(countArgs, id = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val countArgs = CountArgs(...)
 * update(countArgs, id = ..., parameter = value)
 * ```
 * 
 * 
 * @see CountArgs
 */
internal fun update(
    countArgs: CountArgs,
    id: String,
    count: Int = countArgs.count,
): Unit = update(
    id = id,
    count = count,
)
````
