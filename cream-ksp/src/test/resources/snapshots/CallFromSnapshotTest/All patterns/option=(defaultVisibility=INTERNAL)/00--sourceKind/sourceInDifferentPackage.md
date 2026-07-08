## Input:me.tbsten.cream.generated.consume

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom
import me.tbsten.cream.generated.other.OtherPackageArgs

@CallFrom(OtherPackageArgs::class)
public fun consume(name: String, extra: Int) {
}
```

## Input:me.tbsten.cream.generated.other.OtherPackageArgs

```kt
package me.tbsten.cream.generated.other

import kotlin.String

public data class OtherPackageArgs(
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
 * OtherPackageArgs -> consume() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val otherPackageArgs = OtherPackageArgs(...)
 * consume(otherPackageArgs, extra = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val otherPackageArgs = OtherPackageArgs(...)
 * consume(otherPackageArgs, extra = ..., parameter = value)
 * ```
 * 
 * 
 * @see OtherPackageArgs
 */
internal fun consume(
    otherPackageArgs: me.tbsten.cream.generated.other.OtherPackageArgs,
    name: String = otherPackageArgs.name,
    extra: Int,
): Unit = consume(
    name = name,
    extra = extra,
)
````
