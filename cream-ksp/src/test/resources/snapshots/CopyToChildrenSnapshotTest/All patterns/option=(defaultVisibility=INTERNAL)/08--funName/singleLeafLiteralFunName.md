## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren(funName = "toState")
public sealed interface Source {
  public val id: String

  public data class Only(
    override val id: String,
  ) : Source
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
// file: CopyToChildren__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.Only copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toState()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toState(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Only
 */
internal fun  me.tbsten.cream.generated.Source.toState(
    id: String = this.id,
) : me.tbsten.cream.generated.Source.Only = me.tbsten.cream.generated.Source.Only(
    id = id,
)
````
