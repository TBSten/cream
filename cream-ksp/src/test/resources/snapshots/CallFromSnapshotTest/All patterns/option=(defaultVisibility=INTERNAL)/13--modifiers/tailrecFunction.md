## Input:me.tbsten.cream.generated.countDown

```kt
package me.tbsten.cream.generated

import kotlin.Int
import me.tbsten.cream.CallFrom

@CallFrom(CountArgs::class)
public tailrec fun countDown(n: Int): Int = if (n <= 0) 0 else countDown(n - 1)

public data class CountArgs(
  public val n: Int,
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
// file: CallFrom__countDown.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [countDown])
 * 
 * CountArgs -> countDown() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val countArgs = CountArgs(...)
 * countDown(countArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val countArgs = CountArgs(...)
 * countDown(countArgs, parameter = value)
 * ```
 * 
 * 
 * @see CountArgs
 */
internal fun countDown(
    countArgs: CountArgs,
    n: Int = countArgs.n,
): Int = countDown(
    n = n,
)
````
