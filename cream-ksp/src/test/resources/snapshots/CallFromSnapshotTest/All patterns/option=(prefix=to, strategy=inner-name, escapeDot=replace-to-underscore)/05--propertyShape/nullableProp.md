## Input:me.tbsten.cream.generated.describe

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(DescribeArgs::class)
public fun describe(note: String?) {
}

public data class DescribeArgs(
  public val note: String?,
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
// file: CallFrom__describe.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [describe])
 * 
 * DescribeArgs -> describe() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val describeArgs = DescribeArgs(...)
 * describe(describeArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val describeArgs = DescribeArgs(...)
 * describe(describeArgs, parameter = value)
 * ```
 * 
 * 
 * @see DescribeArgs
 */
public fun describe(
    describeArgs: DescribeArgs,
    note: String? = describeArgs.note,
): Unit = describe(
    note = note,
)
````
