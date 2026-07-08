## Input:me.tbsten.cream.generated.consume

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(Args::class)
public fun consume(name: String, extra: Int) {
}

public class Args(
  public val name: String,
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
// file: CallFrom__consume.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [consume])
 * 
 * Args -> consume() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val args = Args(...)
 * consume(args, extra = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val args = Args(...)
 * consume(args, extra = ..., parameter = value)
 * ```
 * 
 * 
 * @see Args
 */
internal fun consume(
    args: Args,
    name: String = args.name,
    extra: Int,
): Unit = consume(
    name = name,
    extra = extra,
)
````
