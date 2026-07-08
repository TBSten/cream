## Input:me.tbsten.cream.generated.register

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(Outer.Args::class)
public fun register(name: String) {
}

public class Outer {
  public data class Args(
    public val name: String,
  )
}
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
// file: CallFrom__register.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [register])
 * 
 * Outer.Args -> register() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val args = Outer.Args(...)
 * register(args)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val args = Outer.Args(...)
 * register(args, parameter = value)
 * ```
 * 
 * 
 * @see Outer.Args
 */
public fun register(
    args: Outer.Args,
    name: String = args.name,
): Unit = register(
    name = name,
)
````
