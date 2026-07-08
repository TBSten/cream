## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  public val alpha: String

  @CopyToChildren.Map("alpha")
  public val beta: String

  public data class Child(
    override val alpha: String,
  ) : Source {
    override val beta: String
      get() = alpha
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
 * Source -> Source.Child copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceChild()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceChild(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child
 */
internal fun  me.tbsten.cream.generated.Source.copyToSourceChild(
    alpha: String = this.beta,
) : me.tbsten.cream.generated.Source.Child = me.tbsten.cream.generated.Source.Child(
    alpha = alpha,
)
````
