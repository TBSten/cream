## Input:me.tbsten.cream.generated.Joiner

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

public class Joiner(
  public val `value`: String,
) {
  @CallFrom(JoinArgs::class)
  public infix fun join(other: String): String = value + other
}

public data class JoinArgs(
  public val other: String,
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
// file: CallFrom__Joiner_join.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [Joiner.join])
 * 
 * JoinArgs -> Joiner.join() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val joinArgs = JoinArgs(...)
 * joiner.join(joinArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val joinArgs = JoinArgs(...)
 * joiner.join(joinArgs, parameter = value)
 * ```
 * 
 * 
 * @see JoinArgs
 */
public fun me.tbsten.cream.generated.Joiner.join(
    joinArgs: JoinArgs,
    other: String = joinArgs.other,
): String = join(
    other = other,
)
````
