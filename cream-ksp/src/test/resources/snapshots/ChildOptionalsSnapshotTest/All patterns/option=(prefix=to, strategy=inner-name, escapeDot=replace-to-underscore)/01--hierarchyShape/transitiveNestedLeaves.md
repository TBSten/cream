## Input:me.tbsten.cream.generated.Root

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ChildOptionals

@ChildOptionals
public sealed interface Root {
  public sealed interface Middle : Root {
    public data class Leaf(
      public val `data`: String,
    ) : Middle
  }

  public object Other : Root
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
// file: ChildOptionals__Root.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ChildOptionals] annotation of [Root])
 * 
 * Nullable accessor on [Root] exposing [Root.Middle.Leaf.data].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Root = /* one of the subtypes */
 * val data: String? = state.data
 * ```
 * 
 * 
 * @see Root
 * @see Root.Middle.Leaf.data
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Root.data: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Root.Middle.Leaf -> data
        else -> null
    }
````
