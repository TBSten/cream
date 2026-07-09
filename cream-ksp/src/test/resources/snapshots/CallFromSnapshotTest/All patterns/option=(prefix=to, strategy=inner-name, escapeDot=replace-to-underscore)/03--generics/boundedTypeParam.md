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
public fun <T : Comparable<T>> sort(
    sortArgs: SortArgs<T>,
    item: T = sortArgs.item,
): Unit = sort(
    item = item,
)
````
