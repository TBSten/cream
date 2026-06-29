## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyTo

@CopyTo(Source.Target::class)
public data class Source(
  public val name: String,
) {
  public data class Target(
    public val name: String,
    public val extra: Int,
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
// file: CopyTo__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Source.Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceTarget(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceTarget(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Target
 */
internal fun  me.tbsten.cream.generated.Source.copyToSourceTarget(
    name: String = this.name,
    extra: Int,
) : me.tbsten.cream.generated.Source.Target = me.tbsten.cream.generated.Source.Target(
    name = name,
    extra = extra,
)
````
