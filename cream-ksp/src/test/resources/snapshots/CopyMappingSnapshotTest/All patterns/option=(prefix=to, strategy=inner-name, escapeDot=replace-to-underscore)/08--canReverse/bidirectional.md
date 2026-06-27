## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyMapping

@CopyMapping(
  Source::class,
  Target::class,
  canReverse = true,
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
 * val target = source.to_Target(targetOnly = targetOnly)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Target(targetOnly = targetOnly, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  me.tbsten.cream.generated.Source.to_Target(
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
 * val target = source.to_Source(sourceOnly = sourceOnly)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Target(...)
 * val target = source.to_Source(sourceOnly = sourceOnly, property = value)
 * ```
 * 
 * 
 * @see Target
 * @see Source
 */
public fun  me.tbsten.cream.generated.Target.to_Source(
    shared: String = this.shared,
    sourceOnly: Int,
) : me.tbsten.cream.generated.Source = me.tbsten.cream.generated.Source(
    shared = shared,
    sourceOnly = sourceOnly,
)
````
