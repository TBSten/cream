## Input:me.tbsten.cream.generated.track

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(TrackArgs::class)
public fun track(name: String, @CallFrom.Exclude count: Int) {
}

public data class TrackArgs(
  public val name: String,
  public val count: Int,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INTERNAL")
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
internal fun track(
    trackArgs: TrackArgs,
    name: String = trackArgs.name,
    count: Int,
): Unit = track(
    name = name,
    count = count,
)
````
