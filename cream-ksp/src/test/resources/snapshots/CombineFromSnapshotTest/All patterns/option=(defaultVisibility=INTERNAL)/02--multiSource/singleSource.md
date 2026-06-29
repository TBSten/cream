## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineFrom

@CombineFrom(SourceA::class)
public data class Target(
  public val name: String,
  public val extra: Int,
)

public data class SourceA(
  public val name: String,
  public val extra: Int,
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
// file: CombineFrom__Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [Target])
 * 
 * [SourceA] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val target = sourceA.copyToTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val target = sourceA.copyToTarget(property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see Target
 */
internal fun  me.tbsten.cream.generated.SourceA.copyToTarget(
    name: String = this.name,
    extra: Int = this.extra,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    name = name,
    extra = extra,
)
````
