## Input:me.tbsten.cream.generated.Root

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Root

public sealed interface Mid : Root

public data class Leaf<T>(
  @ParentOptional
  public val label: String,
  public val `value`: T,
) : Mid
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
// file: ParentOptional__Mid.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Leaf.label])
 * 
 * Nullable accessor on [Mid] exposing [Leaf.label].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Mid = /* one of the subtypes */
 * val label: String? = state.label
 * ```
 * 
 * 
 * @see Mid
 * @see Leaf.label
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Mid.label: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Leaf<*> -> label
        else -> null
    }

// ----- next file -----

// file: ParentOptional__Root.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Leaf.label])
 * 
 * Nullable accessor on [Root] exposing [Leaf.label].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Root = /* one of the subtypes */
 * val label: String? = state.label
 * ```
 * 
 * 
 * @see Root
 * @see Leaf.label
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Root.label: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Leaf<*> -> label
        else -> null
    }
````
