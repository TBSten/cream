## Input:me.tbsten.cream.generated.track

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(TrackArgs::class)
public fun track(name: String, @CallFrom.Exclude count: Int = 0) {
}

public data class TrackArgs(
  public val name: String,
  public val count: Int,
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
 * track(trackArgs, count = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val trackArgs = TrackArgs(...)
 * track(trackArgs, count = ..., parameter = value)
 * ```
 * 
 * 
 * @see TrackArgs
 */
public fun track(
    trackArgs: TrackArgs,
    name: String = trackArgs.name,
    count: Int,
): Unit = track(
    name = name,
    count = count,
)
````
