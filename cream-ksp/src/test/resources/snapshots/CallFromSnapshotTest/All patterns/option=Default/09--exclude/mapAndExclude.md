## Input:me.tbsten.cream.generated.track

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(TrackArgs::class)
public fun track(@CallFrom.Map("sourceName") @CallFrom.Exclude paramName: String, shared: String) {
}

public data class TrackArgs(
  public val sourceName: String,
  public val shared: String,
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
// file: CallFrom__track.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [track])
 * 
 * TrackArgs -> track() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val trackArgs = TrackArgs(...)
 * track(trackArgs, paramName = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val trackArgs = TrackArgs(...)
 * track(trackArgs, paramName = ..., parameter = value)
 * ```
 * 
 * 
 * @see TrackArgs
 */
public fun track(
    trackArgs: TrackArgs,
    paramName: String,
    shared: String = trackArgs.shared,
): Unit = track(
    paramName = paramName,
    shared = shared,
)
````
