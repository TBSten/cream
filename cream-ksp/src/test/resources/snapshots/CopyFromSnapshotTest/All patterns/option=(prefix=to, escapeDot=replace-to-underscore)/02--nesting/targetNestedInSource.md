## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyFrom

public data class Source(
  public val name: String,
) {
  @CopyFrom(Source::class)
  public data class Target(
    public val name: String,
    public val extra: Int,
  )
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "replace-to-underscore")
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
// file: CopyFrom__Source.Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Source.Target])
 * 
 * Source -> Source.Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Source_Target(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Source_Target(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Target
 */
public fun  me.tbsten.cream.generated.Source.to_Source_Target(
    name: String = this.name,
    extra: Int,
) : me.tbsten.cream.generated.Source.Target = me.tbsten.cream.generated.Source.Target(
    name = name,
    extra = extra,
)
````
