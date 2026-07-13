## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  @CopyToChildren.Map("targetName")
  @CopyToChildren.Exclude
  public val sourceName: String

  public val shared: String

  public data class Child(
    public val targetName: String,
    override val shared: String,
  ) : Source {
    override val sourceName: String
      get() = targetName
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
 * Source -> Source.Child copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToChild(targetName = targetName)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToChild(targetName = targetName, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child
 */
internal fun  me.tbsten.cream.generated.Source.copyToChild(
    targetName: String,
    shared: String = this.shared,
) : me.tbsten.cream.generated.Source.Child = me.tbsten.cream.generated.Source.Child(
    targetName = targetName,
    shared = shared,
)
````
