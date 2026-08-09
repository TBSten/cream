## Input:me.tbsten.cream.generated.process

```kt
package me.tbsten.cream.generated

import me.tbsten.cream.CallFrom

@CallFrom(ProcessArgs::class)
public fun <T> process(item: T) {
}

public data class ProcessArgs<T>(
  public val item: T,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
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
// file: CallFrom__process.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [process])
 * 
 * ProcessArgs -> process() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val processArgs = ProcessArgs(...)
 * process(processArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val processArgs = ProcessArgs(...)
 * process(processArgs, parameter = value)
 * ```
 * 
 * 
 * @see ProcessArgs
 */
public fun <T : Any?> process(
    processArgs: ProcessArgs<T>,
    item: T = processArgs.item,
): Unit = process(
    item = item,
)
````
