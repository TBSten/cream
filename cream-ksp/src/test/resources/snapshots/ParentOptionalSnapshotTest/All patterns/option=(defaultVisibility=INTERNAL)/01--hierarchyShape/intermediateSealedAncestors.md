## Input:me.tbsten.cream.generated.Root

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Root {
  public sealed interface Middle : Root {
    public data class Leaf(
      @ParentOptional
      public val `data`: String,
    ) : Middle
  }

  public object Other : Root
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
// file: ParentOptional__Root.Middle.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Root.Middle.Leaf.data])
 * 
 * Nullable accessor on [Root.Middle] exposing [Root.Middle.Leaf.data].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Root.Middle = /* one of the subtypes */
 * val data: String? = state.data
 * ```
 * 
 * 
 * @see Root.Middle
 * @see Root.Middle.Leaf.data
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Root.Middle.data: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Root.Middle.Leaf -> data
        else -> null
    }

// ----- next file -----

// file: ParentOptional__Root.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Root.Middle.Leaf.data])
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
internal val me.tbsten.cream.generated.Root.data: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Root.Middle.Leaf -> data
        else -> null
    }
````
