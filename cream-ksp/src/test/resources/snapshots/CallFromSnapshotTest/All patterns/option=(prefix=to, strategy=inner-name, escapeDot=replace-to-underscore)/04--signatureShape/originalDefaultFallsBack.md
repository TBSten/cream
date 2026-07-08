## Input:me.tbsten.cream.generated.greet

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(GreetArgs::class)
public fun greet(name: String, punctuation: String = "!") {
}

public data class GreetArgs(
  public val name: String,
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
 * greet(greetArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val greetArgs = GreetArgs(...)
 * greet(greetArgs, parameter = value)
 * ```
 * 
 * 
 * @see GreetArgs
 */
public fun greet(
    greetArgs: GreetArgs,
    name: String = greetArgs.name,
): Unit = greet(
    name = name,
)
````
