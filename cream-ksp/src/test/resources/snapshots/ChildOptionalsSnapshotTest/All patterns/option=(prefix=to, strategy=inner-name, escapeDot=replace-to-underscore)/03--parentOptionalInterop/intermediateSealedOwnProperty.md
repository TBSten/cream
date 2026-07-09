## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.ParentOptional

@ChildOptionals
public sealed interface Source {
  public object Other : Source
}

public sealed class Middle(
  @ParentOptional
  public val session: String,
) : Source

public class Leaf(
  session: String,
  public val extra: Int,
) : Middle(session)
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
// file: ChildOptionals__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ChildOptionals] annotation of [Source])
 * 
 * Nullable accessor on [Source] exposing [Leaf.extra].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val extra: Int? = state.extra
 * ```
 * 
 * 
 * @see Source
 * @see Leaf.extra
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.extra: Int?
    get() = when (this) {
        is me.tbsten.cream.generated.Leaf -> extra
        else -> null
    }

/**
 * (Auto generate by @[ParentOptional] annotation of [Middle.session])
 * 
 * Nullable accessor on [Source] exposing [Middle.session].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val session: String? = state.session
 * ```
 * 
 * 
 * @see Source
 * @see Middle.session
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.session: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Middle -> session
        else -> null
    }
````
