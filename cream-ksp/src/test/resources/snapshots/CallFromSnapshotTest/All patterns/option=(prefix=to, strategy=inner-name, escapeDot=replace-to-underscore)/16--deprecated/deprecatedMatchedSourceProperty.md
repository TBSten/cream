## Input:me.tbsten.cream.generated.track

```kt
package me.tbsten.cream.generated

import kotlin.Deprecated
import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(TrackArgs::class)
public fun track(name: String, legacyId: String) {
}

public data class TrackArgs(
  public val name: String,
  @Deprecated("Use uuid instead.")
  public val legacyId: String,
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
w: file://<TMPDIR>/Kotlin-Compilation<N>/ksp/sources/kotlin/me/tbsten/cream/generated/CallFrom__track.kt:31:34 'val legacyId: String' is deprecated. Use uuid instead.
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
 * track(trackArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val trackArgs = TrackArgs(...)
 * track(trackArgs, parameter = value)
 * ```
 * 
 * 
 * @see TrackArgs
 */
@Deprecated("Use uuid instead.")
public fun track(
    trackArgs: TrackArgs,
    name: String = trackArgs.name,
    legacyId: String = trackArgs.legacyId,
): Unit = track(
    name = name,
    legacyId = legacyId,
)
````
