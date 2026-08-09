## Input:me.tbsten.cream.generated.count

```kt
package me.tbsten.cream.generated

import kotlin.Int
import me.tbsten.cream.CallFrom

@CallFrom(TallyArgs::class)
public fun count(total: Int) {
}

public data class TallyArgs<T>(
  public val item: T,
  public val total: Int,
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
// file: CallFrom__count.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [count])
 * 
 * TallyArgs -> count() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val tallyArgs = TallyArgs(...)
 * count(tallyArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val tallyArgs = TallyArgs(...)
 * count(tallyArgs, parameter = value)
 * ```
 * 
 * 
 * @see TallyArgs
 */
public fun <T : Any?> count(
    tallyArgs: TallyArgs<T>,
    total: Int = tallyArgs.total,
): Unit = count(
    total = total,
)
````
