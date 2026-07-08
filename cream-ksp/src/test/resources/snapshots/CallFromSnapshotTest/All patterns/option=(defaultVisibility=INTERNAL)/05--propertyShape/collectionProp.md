## Input:me.tbsten.cream.generated.collect

```kt
package me.tbsten.cream.generated

import kotlin.String
import kotlin.collections.List
import me.tbsten.cream.CallFrom

@CallFrom(CollectArgs::class)
public fun collect(names: List<String>) {
}

public data class CollectArgs(
  public val names: List<String>,
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
// file: CallFrom__collect.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [collect])
 * 
 * CollectArgs -> collect() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val collectArgs = CollectArgs(...)
 * collect(collectArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val collectArgs = CollectArgs(...)
 * collect(collectArgs, parameter = value)
 * ```
 * 
 * 
 * @see CollectArgs
 */
internal fun collect(
    collectArgs: CollectArgs,
    names: kotlin.collections.List<String> = collectArgs.names,
): Unit = collect(
    names = names,
)
````
