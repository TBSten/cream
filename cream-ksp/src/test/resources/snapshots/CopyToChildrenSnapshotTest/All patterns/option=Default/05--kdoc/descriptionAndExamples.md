## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.KDoc

@CopyToChildren(kdoc = KDoc(description = "Use this only when migrating.", examples = ["# Recommended\n\nval target = source.copyToSourceChild(name = name)"]))
public sealed interface Source {
  public data class Child(
    public val name: String,
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
// file: CopyToChildren__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.Child copy function.
 * 
 * Use this only when migrating.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceChild(name = name)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceChild(name = name, property = value)
 * ```
 * 
 * # Recommended
 * 
 * val target = source.copyToSourceChild(name = name)
 * 
 * 
 * @see Source
 * @see Source.Child
 */
public fun  me.tbsten.cream.generated.Source.copyToSourceChild(
    name: String,
) : me.tbsten.cream.generated.Source.Child = me.tbsten.cream.generated.Source.Child(
    name = name,
)
````
