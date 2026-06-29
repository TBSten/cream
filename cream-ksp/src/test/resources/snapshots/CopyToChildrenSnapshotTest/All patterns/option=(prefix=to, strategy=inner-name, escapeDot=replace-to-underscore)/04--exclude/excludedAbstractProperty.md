## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  public val name: String

  @CopyToChildren.Exclude
  public val count: Int

  public data class Child(
    override val name: String,
    override val count: Int,
  ) : Source
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "inner-name")
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
// file: CopyToChildren__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.Child copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Child(count = count)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Child(count = count, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child
 */
public fun  me.tbsten.cream.generated.Source.to_Child(
    name: String = this.name,
    count: Int,
) : me.tbsten.cream.generated.Source.Child = me.tbsten.cream.generated.Source.Child(
    name = name,
    count = count,
)
````
