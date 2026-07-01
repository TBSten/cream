## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyMapping
import me.tbsten.cream.CopyVisibility

@CopyMapping(
  Source::class,
  Target::class,
  canReverse = true,
  visibility = CopyVisibility.INTERNAL,
)
public object Mapping

public data class Source(
  public val shared: String,
  public val sourceOnly: Int,
)

public data class Target(
  public val shared: String,
  public val targetOnly: Boolean,
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
    arg("nonCopyableStrategy", "INHERIT" /* default */)
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
    shared: String = this.shared,
    targetOnly: Boolean,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    shared = shared,
    targetOnly = targetOnly,
)

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * Target -> Source copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Target(...)
 * val target = source.copyToSource(sourceOnly = sourceOnly)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Target(...)
 * val target = source.copyToSource(sourceOnly = sourceOnly, property = value)
 * ```
 * 
 * 
 * @see Target
 * @see Source
 */
internal fun  me.tbsten.cream.generated.Target.copyToSource(
    shared: String = this.shared,
    sourceOnly: Int,
) : me.tbsten.cream.generated.Source = me.tbsten.cream.generated.Source(
    shared = shared,
    sourceOnly = sourceOnly,
)
````
