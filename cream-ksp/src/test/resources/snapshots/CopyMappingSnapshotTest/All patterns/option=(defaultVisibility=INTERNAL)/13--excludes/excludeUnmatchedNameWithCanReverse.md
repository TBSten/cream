## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.String
import me.tbsten.cream.CopyMapping

@CopyMapping(
  Source::class,
  Target::class,
  canReverse = true,
  excludes = ["missing"],
)
public object Mapping

public data class Source(
  public val shared: String,
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
    arg("autoValueClassMapping", "true" /* default */)
}
```

## Output:ExitCode

```kt
OK
```

## Output:Console

```kt
w: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Mapping.kt:13: excludes entry 'missing' has no effect: not a matched property
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
 * val target = source.copyToSource()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Target(...)
 * val target = source.copyToSource(property = value)
 * ```
 * 
 * 
 * @see Target
 * @see Source
 */
internal fun  me.tbsten.cream.generated.Target.copyToSource(
    shared: String = this.shared,
) : me.tbsten.cream.generated.Source = me.tbsten.cream.generated.Source(
    shared = shared,
)
````
