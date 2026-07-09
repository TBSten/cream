## Input:me.tbsten.cream.generated.record

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(RecordArgs::class)
public fun record(
  name: String,
  count: Int,
  enabled: Boolean,
  ratio: Double,
) {
}

public data class RecordArgs(
  public val name: String,
  public val count: Int,
  public val enabled: Boolean,
  public val ratio: Double,
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
// file: CallFrom__record.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [record])
 * 
 * RecordArgs -> record() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val recordArgs = RecordArgs(...)
 * record(recordArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val recordArgs = RecordArgs(...)
 * record(recordArgs, parameter = value)
 * ```
 * 
 * 
 * @see RecordArgs
 */
public fun record(
    recordArgs: RecordArgs,
    name: String = recordArgs.name,
    count: Int = recordArgs.count,
    enabled: Boolean = recordArgs.enabled,
    ratio: Double = recordArgs.ratio,
): Unit = record(
    name = name,
    count = count,
    enabled = enabled,
    ratio = ratio,
)
````
