## Input:me.tbsten.cream.generated.Factory

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

public class Factory {
  public companion object {
    @CallFrom(MakeArgs::class)
    public fun make(`value`: String) {
    }
  }
}

public data class MakeArgs(
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
// file: CallFrom__Factory_Companion_make.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [Factory.Companion.make])
 * 
 * MakeArgs -> Factory.Companion.make() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val makeArgs = MakeArgs(...)
 * companion.make(makeArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val makeArgs = MakeArgs(...)
 * companion.make(makeArgs, parameter = value)
 * ```
 * 
 * 
 * @see MakeArgs
 */
public fun me.tbsten.cream.generated.Factory.Companion.make(
    makeArgs: MakeArgs,
    value: String = makeArgs.value,
): Unit = make(
    value = value,
)
````
