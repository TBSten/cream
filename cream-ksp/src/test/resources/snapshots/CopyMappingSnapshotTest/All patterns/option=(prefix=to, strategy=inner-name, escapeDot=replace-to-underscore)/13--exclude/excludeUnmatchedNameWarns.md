## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyMapping

@CopyMapping(
  Source::class,
  Target::class,
  excludes = ["nonexistent"],
)
public object Mapping

public data class Source(
  public val shared: String,
)

public data class Target(
  public val shared: String,
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
    arg("nonCopyableStrategy", "INHERIT" /* default */)
}
```

## Output:ExitCode

```kt
OK
```

## Output:Console

```kt
w: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Mapping.kt:11: excludes entry 'nonexistent' has no effect: not an auto-defaulted parameter
```

## Output:Generated sources

````kt
// file: CopyMapping__Mapping.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * Source -> Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Target()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Target(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  me.tbsten.cream.generated.Source.to_Target(
    shared: String = this.shared,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    shared = shared,
)
````
