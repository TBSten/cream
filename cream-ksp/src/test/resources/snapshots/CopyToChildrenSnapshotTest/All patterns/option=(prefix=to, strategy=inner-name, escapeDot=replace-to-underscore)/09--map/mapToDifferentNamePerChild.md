## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  @CopyToChildren.Map(
    "idA",
    "idB",
  )
  public val sourceId: String

  public data class ChildA(
    public val idA: String,
  ) : Source {
    override val sourceId: String
      get() = idA
  }

  public data class ChildB(
    public val idB: String,
  ) : Source {
    override val sourceId: String
      get() = idB
  }
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
 * Source -> Source.ChildA copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_ChildA()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_ChildA(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.ChildA
 */
public fun  me.tbsten.cream.generated.Source.to_ChildA(
    idA: String = this.sourceId,
) : me.tbsten.cream.generated.Source.ChildA = me.tbsten.cream.generated.Source.ChildA(
    idA = idA,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.ChildB copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_ChildB()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_ChildB(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.ChildB
 */
public fun  me.tbsten.cream.generated.Source.to_ChildB(
    idB: String = this.sourceId,
) : me.tbsten.cream.generated.Source.ChildB = me.tbsten.cream.generated.Source.ChildB(
    idB = idB,
)
````
