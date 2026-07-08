## Input:me.tbsten.cream.generated.sort

```kt
package me.tbsten.cream.generated

import kotlin.Comparable
import me.tbsten.cream.CallFrom

@CallFrom(SortArgs::class)
public fun <T : Comparable<T>> sort(item: T) {
}

public data class SortArgs<T : Comparable<T>>(
  public val item: T,
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
// file: CallFrom__sort.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [sort])
 * 
 * SortArgs -> sort() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sortArgs = SortArgs(...)
 * sort(sortArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val sortArgs = SortArgs(...)
 * sort(sortArgs, parameter = value)
 * ```
 * 
 * 
 * @see SortArgs
 */
internal fun <T : Comparable<T>> sort(
    sortArgs: SortArgs<T>,
    item: T = sortArgs.item,
): Unit = sort(
    item = item,
)
````
