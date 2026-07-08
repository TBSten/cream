## Input:me.tbsten.cream.generated.load

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(LoadArgs::class)
public fun load(id: UserId) {
}

public data class LoadArgs(
  public val id: String,
)

public typealias UserId = String
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
internal fun load(
    loadArgs: LoadArgs,
    id: UserId = loadArgs.id,
): Unit = load(
    id = id,
)
````
