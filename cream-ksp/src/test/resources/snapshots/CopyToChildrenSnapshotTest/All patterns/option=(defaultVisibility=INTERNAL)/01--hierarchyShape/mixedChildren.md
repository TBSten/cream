## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  public data class DataChild(
    public val a: String,
  ) : Source

  public object ObjectChild : Source

  public sealed interface Branch : Source {
    public data class Leaf(
      public val b: Int,
    ) : Branch
  }
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
 * Source -> Source.Branch.Leaf copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceBranchLeaf(b = b)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceBranchLeaf(b = b, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Branch.Leaf
 */
internal fun  me.tbsten.cream.generated.Source.copyToSourceBranchLeaf(
    b: Int,
) : me.tbsten.cream.generated.Source.Branch.Leaf = me.tbsten.cream.generated.Source.Branch.Leaf(
    b = b,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.DataChild copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceDataChild(a = a)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceDataChild(a = a, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.DataChild
 */
internal fun  me.tbsten.cream.generated.Source.copyToSourceDataChild(
    a: String,
) : me.tbsten.cream.generated.Source.DataChild = me.tbsten.cream.generated.Source.DataChild(
    a = a,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.ObjectChild copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceObjectChild()
 * ```
 * 
 * 
 * @see Source
 * @see Source.ObjectChild
 */
internal fun me.tbsten.cream.generated.Source.copyToSourceObjectChild() = me.tbsten.cream.generated.Source.ObjectChild
````
