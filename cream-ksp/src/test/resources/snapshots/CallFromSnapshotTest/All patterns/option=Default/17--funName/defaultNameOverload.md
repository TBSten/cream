## Input:me.tbsten.cream.generated.build

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(BuildArgs::class)
public fun build(name: String, size: Int) {
}

public data class BuildArgs(
  public val name: String,
  public val size: Int,
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
// file: CallFrom__build.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [build])
 * 
 * BuildArgs -> build() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val buildArgs = BuildArgs(...)
 * build(buildArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val buildArgs = BuildArgs(...)
 * build(buildArgs, parameter = value)
 * ```
 * 
 * 
 * @see BuildArgs
 */
public fun build(
    buildArgs: BuildArgs,
    name: String = buildArgs.name,
    size: Int = buildArgs.size,
): Unit = build(
    name = name,
    size = size,
)
````
