## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyFrom

@CopyFrom(Source::class)
public data class Target(
  public val name: String,
  @CopyFrom.Exclude
  public val targetOnly: Int,
)

public data class Source(
  public val name: String,
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
w: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Target.kt:11: @Exclude on 'targetOnly' has no effect: not a matched property
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
 * val target = source.copyToTarget(targetOnly = targetOnly)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(targetOnly = targetOnly, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
internal fun  me.tbsten.cream.generated.Source.copyToTarget(
    name: String = this.name,
    targetOnly: Int,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    name = name,
    targetOnly = targetOnly,
)
````
