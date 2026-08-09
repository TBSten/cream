## Input:me.tbsten.cream.generated.greet

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(GreetArgs::class)
public fun String.greet(name: String): String = this + name

public data class GreetArgs(
  public val name: String,
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
// file: CallFrom__greet.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [greet])
 * 
 * GreetArgs -> greet() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val greetArgs = GreetArgs(...)
 * string.greet(greetArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val greetArgs = GreetArgs(...)
 * string.greet(greetArgs, parameter = value)
 * ```
 * 
 * 
 * @see GreetArgs
 */
public fun String.greet(
    greetArgs: GreetArgs,
    name: String = greetArgs.name,
): String = greet(
    name = name,
)
````
