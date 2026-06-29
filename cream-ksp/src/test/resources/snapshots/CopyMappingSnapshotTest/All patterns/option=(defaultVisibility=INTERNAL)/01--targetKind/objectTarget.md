## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyMapping

@CopyMapping(
  Source::class,
  Target::class,
)
public object Mapping

public data class Source(
  public val name: String,
)

public object Target
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
 * val target = source.copyToTarget()
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
internal fun me.tbsten.cream.generated.Source.copyToTarget() = me.tbsten.cream.generated.Target
````
