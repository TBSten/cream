## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Source

public sealed class Middle(
  @ParentOptional(propertyName = "acc")
  public val x: String,
) : Source

public class Leaf(
  x: String,
  @ParentOptional(propertyName = "acc")
  public val y: String,
) : Middle(x)
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
// file: ParentOptional__Middle.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Leaf.y])
 * 
 * Nullable accessor on [Middle] exposing [Leaf.y].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Middle = /* one of the subtypes */
 * val acc: String? = state.acc
 * ```
 * 
 * 
 * @see Middle
 * @see Leaf.y
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Middle.acc: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Leaf -> y
        else -> null
    }

// ----- next file -----

// file: ParentOptional__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Leaf.y])
 * 
 * Nullable accessor on [Source] exposing [Leaf.y] / [Middle.x].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val acc: String? = state.acc
 * ```
 * 
 * 
 * @see Source
 * @see Leaf.y
 * @see Middle.x
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.acc: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Leaf -> y
        is me.tbsten.cream.generated.Middle -> x
        else -> null
    }
````
