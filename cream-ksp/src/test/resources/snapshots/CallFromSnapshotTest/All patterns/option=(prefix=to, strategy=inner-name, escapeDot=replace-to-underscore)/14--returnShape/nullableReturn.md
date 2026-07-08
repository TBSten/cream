## Input:me.tbsten.cream.generated.find

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(FindArgs::class)
public fun find(id: String): String? = null

public data class FindArgs(
  public val id: String,
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
// file: CallFrom__find.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [find])
 * 
 * FindArgs -> find() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val findArgs = FindArgs(...)
 * find(findArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val findArgs = FindArgs(...)
 * find(findArgs, parameter = value)
 * ```
 * 
 * 
 * @see FindArgs
 */
public fun find(
    findArgs: FindArgs,
    id: String = findArgs.id,
): String? = find(
    id = id,
)
````
