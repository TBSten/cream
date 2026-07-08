## Input:me.tbsten.cream.generated.DataProcessor

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

public class DataProcessor {
  @CallFrom(ProcessArgs::class)
  public fun process(`value`: String) {
  }
}

public data class ProcessArgs(
  public val `value`: String,
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
// file: CallFrom__DataProcessor_process.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [DataProcessor.process])
 * 
 * ProcessArgs -> DataProcessor.process() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val processArgs = ProcessArgs(...)
 * dataProcessor.process(processArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val processArgs = ProcessArgs(...)
 * dataProcessor.process(processArgs, parameter = value)
 * ```
 * 
 * 
 * @see ProcessArgs
 */
public fun me.tbsten.cream.generated.DataProcessor.process(
    processArgs: ProcessArgs,
    value: String = processArgs.value,
): Unit = process(
    value = value,
)
````
