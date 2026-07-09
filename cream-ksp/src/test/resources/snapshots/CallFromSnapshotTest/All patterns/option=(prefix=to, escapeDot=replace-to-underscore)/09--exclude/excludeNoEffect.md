## Input:me.tbsten.cream.generated.track

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(TrackArgs::class)
public fun track(name: String, @CallFrom.Exclude paramOnly: Int) {
}

public data class TrackArgs(
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
w: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.track.kt:8: @Exclude on 'paramOnly' has no effect: not a matched property
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
 * track(trackArgs, paramOnly = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val trackArgs = TrackArgs(...)
 * track(trackArgs, paramOnly = ..., parameter = value)
 * ```
 * 
 * 
 * @see TrackArgs
 */
public fun track(
    trackArgs: TrackArgs,
    name: String = trackArgs.name,
    paramOnly: Int,
): Unit = track(
    name = name,
    paramOnly = paramOnly,
)
````
