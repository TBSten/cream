## Input:me.tbsten.cream.generated.load

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(LoadArgs::class)
public suspend fun load(id: String): Int = id.length

public data class LoadArgs(
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
// file: CallFrom__load.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [load])
 * 
 * LoadArgs -> load() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val loadArgs = LoadArgs(...)
 * load(loadArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val loadArgs = LoadArgs(...)
 * load(loadArgs, parameter = value)
 * ```
 * 
 * 
 * @see LoadArgs
 */
internal suspend fun load(
    loadArgs: LoadArgs,
    id: String = loadArgs.id,
): Int = load(
    id = id,
)
````
