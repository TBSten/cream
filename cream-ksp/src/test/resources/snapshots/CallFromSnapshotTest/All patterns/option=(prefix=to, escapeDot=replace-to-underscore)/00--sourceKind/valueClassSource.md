## Input:me.tbsten.cream.generated.consume

```kt
package me.tbsten.cream.generated

import kotlin.String
import kotlin.jvm.JvmInline
import me.tbsten.cream.CallFrom

@CallFrom(Args::class)
public fun consume(`value`: String) {
}

@JvmInline
public value class Args(
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
 * consume(args)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val args = Args(...)
 * consume(args, parameter = value)
 * ```
 * 
 * 
 * @see Args
 */
public fun consume(
    args: Args,
    value: String = args.value,
): Unit = consume(
    value = value,
)
````
