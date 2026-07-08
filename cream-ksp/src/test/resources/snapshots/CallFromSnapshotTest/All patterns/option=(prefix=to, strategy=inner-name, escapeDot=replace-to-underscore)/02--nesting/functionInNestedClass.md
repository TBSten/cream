## Input:me.tbsten.cream.generated.Outer

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

public class Outer {
  public class Inner {
    @CallFrom(ProcessArgs::class)
    public fun process(`value`: String) {
    }
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
// file: CallFrom__Outer_Inner_process.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [Outer.Inner.process])
 * 
 * ProcessArgs -> Outer.Inner.process() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val processArgs = ProcessArgs(...)
 * inner.process(processArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val processArgs = ProcessArgs(...)
 * inner.process(processArgs, parameter = value)
 * ```
 * 
 * 
 * @see ProcessArgs
 */
public fun me.tbsten.cream.generated.Outer.Inner.process(
    processArgs: ProcessArgs,
    value: String = processArgs.value,
): Unit = process(
    value = value,
)
````
