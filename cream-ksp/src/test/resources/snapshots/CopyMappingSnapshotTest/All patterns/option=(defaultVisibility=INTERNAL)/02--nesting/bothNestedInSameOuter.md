## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyMapping

@CopyMapping(
  Outer.Source::class,
  Outer.Target::class,
)
public object Mapping

public class Outer {
  public data class Source(
    public val name: String,
  )

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
 * Outer.Source -> Outer.Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToOuterTarget(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToOuterTarget(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Outer.Source
 * @see Outer.Target
 */
internal fun  me.tbsten.cream.generated.Outer.Source.copyToOuterTarget(
    name: String = this.name,
    extra: Int,
) : me.tbsten.cream.generated.Outer.Target = me.tbsten.cream.generated.Outer.Target(
    name = name,
    extra = extra,
)
````
