## Input:me.tbsten.cream.generated.render

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(RenderArgs::class)
public fun render(config: Config) {
}

public data class RenderArgs(
  public val config: Config,
)

public data class Config(
  public val id: String,
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
// file: CallFrom__render.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [render])
 * 
 * RenderArgs -> render() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val renderArgs = RenderArgs(...)
 * render(renderArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val renderArgs = RenderArgs(...)
 * render(renderArgs, parameter = value)
 * ```
 * 
 * 
 * @see RenderArgs
 */
public fun render(
    renderArgs: RenderArgs,
    config: Config = renderArgs.config,
): Unit = render(
    config = config,
)
````
