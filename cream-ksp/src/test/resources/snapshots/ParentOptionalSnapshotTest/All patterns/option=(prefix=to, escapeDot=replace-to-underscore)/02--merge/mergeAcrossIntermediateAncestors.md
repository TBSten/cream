## Input:me.tbsten.cream.generated.Root

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Root {
  public data class Direct(
    @ParentOptional
    public val id: String,
  ) : Root

  public sealed interface Middle : Root {
    public data class Leaf(
      @ParentOptional
      public val id: String,
    ) : Middle
  }
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "under-package" /* default */)
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
// file: ParentOptional__Root.Middle.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Root.Middle.Leaf.id])
 * 
 * Nullable accessor on [Root.Middle] exposing [Root.Middle.Leaf.id].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Root.Middle = /* one of the subtypes */
 * val id: String? = state.id
 * ```
 * 
 * 
 * @see Root.Middle
 * @see Root.Middle.Leaf.id
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Root.Middle.id: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Root.Middle.Leaf -> id
        else -> null
    }

// ----- next file -----

// file: ParentOptional__Root.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Root.Direct.id])
 * 
 * Nullable accessor on [Root] exposing [Root.Direct.id] / [Root.Middle.Leaf.id].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Root = /* one of the subtypes */
 * val id: String? = state.id
 * ```
 * 
 * 
 * @see Root
 * @see Root.Direct.id
 * @see Root.Middle.Leaf.id
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Root.id: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Root.Direct -> id
        is me.tbsten.cream.generated.Root.Middle.Leaf -> id
        else -> null
    }
````
