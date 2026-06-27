## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyTo

@CopyTo(Source.Middle.Target::class)
public data class Source(
  public val name: String,
) {
  public class Middle {
    public data class Target(
      public val name: String,
      public val extra: Int,
    )
  }
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
 * Source -> Source.Middle.Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Source_Middle_Target(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Source_Middle_Target(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Middle.Target
 */
public fun  me.tbsten.cream.generated.Source.to_Source_Middle_Target(
    name: String = this.name,
    extra: Int,
) : me.tbsten.cream.generated.Source.Middle.Target = me.tbsten.cream.generated.Source.Middle.Target(
    name = name,
    extra = extra,
)
````
