## Input:me.tbsten.cream.generated.processData

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(ProcessDataArgs::class)
public fun processData(data1: String, data2: Int): String = data1

public data class ProcessDataArgs(
  public val data1: String,
  public val data2: Int,
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
// file: CallFrom__processData.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [processData])
 * 
 * ProcessDataArgs -> processData() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val processDataArgs = ProcessDataArgs(...)
 * processData(processDataArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val processDataArgs = ProcessDataArgs(...)
 * processData(processDataArgs, parameter = value)
 * ```
 * 
 * 
 * @see ProcessDataArgs
 */
public fun processData(
    processDataArgs: ProcessDataArgs,
    data1: String = processDataArgs.data1,
    data2: Int = processDataArgs.data2,
): String = processData(
    data1 = data1,
    data2 = data2,
)
````
