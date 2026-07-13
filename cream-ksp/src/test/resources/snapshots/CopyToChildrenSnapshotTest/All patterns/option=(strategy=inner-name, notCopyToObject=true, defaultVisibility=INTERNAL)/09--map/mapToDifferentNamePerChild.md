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
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "inner-name")
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "true")
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
 * Source -> Source.ChildA copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToChildA()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToChildA(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.ChildA
 */
internal fun  me.tbsten.cream.generated.Source.copyToChildA(
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
 * val target = source.copyToChildB()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToChildB(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.ChildB
 */
internal fun  me.tbsten.cream.generated.Source.copyToChildB(
    idB: String = this.sourceId,
) : me.tbsten.cream.generated.Source.ChildB = me.tbsten.cream.generated.Source.ChildB(
    idB = idB,
)
````
