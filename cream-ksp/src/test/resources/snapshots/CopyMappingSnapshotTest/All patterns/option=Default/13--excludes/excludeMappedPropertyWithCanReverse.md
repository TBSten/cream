## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyMapping

@CopyMapping(
  Source::class,
  Target::class,
  canReverse = true,
  properties = [CopyMapping.Map(source = "sourceName", target = "targetName")],
  excludes = ["targetName"],
)
public object Mapping

public data class Source(
  public val sourceName: String,
  public val shared: String,
)

public data class Target(
  public val targetName: String,
  public val shared: String,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INHERIT" /* default */)
    arg("autoValueClassMapping", "true" /* default */)
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
 * val target = source.copyToTarget(targetName = targetName)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(targetName = targetName, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  me.tbsten.cream.generated.Source.copyToTarget(
    targetName: String,
    shared: String = this.shared,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    targetName = targetName,
    shared = shared,
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
 * val target = source.copyToSource(sourceName = sourceName)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Target(...)
 * val target = source.copyToSource(sourceName = sourceName, property = value)
 * ```
 * 
 * 
 * @see Target
 * @see Source
 */
public fun  me.tbsten.cream.generated.Target.copyToSource(
    sourceName: String,
    shared: String = this.shared,
) : me.tbsten.cream.generated.Source = me.tbsten.cream.generated.Source(
    sourceName = sourceName,
    shared = shared,
)
````
