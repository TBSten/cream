## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyTo

public data class Target(
  public val name: String,
  public val extra: Int,
) {
  @CopyTo(Target::class)
  public data class Source(
    public val name: String,
  )
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
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
// file: CopyTo__Target.Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Target.Source])
 * 
 * Target.Source -> Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Target.Source
 * @see Target
 */
public fun  me.tbsten.cream.generated.Target.Source.copyToTarget(
    name: String = this.name,
    extra: Int,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    name = name,
    extra = extra,
)
````
