## Input:me.tbsten.cream.generated.legacyProcess

```kt
package me.tbsten.cream.generated

import kotlin.Deprecated
import kotlin.String
import me.tbsten.cream.CallFrom

@Deprecated("Use processV2 instead.")
@CallFrom(ProcessArgs::class)
public fun legacyProcess(`value`: String) {
}

public data class ProcessArgs(
  public val `value`: String,
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
w: file://<TMPDIR>/Kotlin-Compilation<N>/ksp/sources/kotlin/me/tbsten/cream/generated/CallFrom__legacyProcess.kt:31:11 'fun legacyProcess(value: String): Unit' is deprecated. Use processV2 instead.
```

## Output:Generated sources

````kt
// file: CallFrom__legacyProcess.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [legacyProcess])
 * 
 * ProcessArgs -> legacyProcess() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val processArgs = ProcessArgs(...)
 * legacyProcess(processArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val processArgs = ProcessArgs(...)
 * legacyProcess(processArgs, parameter = value)
 * ```
 * 
 * 
 * @see ProcessArgs
 */
@Deprecated("Use processV2 instead.")
public fun legacyProcess(
    processArgs: ProcessArgs,
    value: String = processArgs.value,
): Unit = legacyProcess(
    value = value,
)
````
