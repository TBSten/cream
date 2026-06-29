## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyFrom

@CopyFrom(Source::class)
public data class Target(
  @CopyFrom.Map("sourceName")
  public val targetName: String,
  public val shared: String,
)

public data class Source(
  public val sourceName: String,
  public val shared: String,
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
// file: CopyFrom__Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Target])
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
    targetName: String = this.sourceName,
    shared: String = this.shared,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    targetName = targetName,
    shared = shared,
)
````
