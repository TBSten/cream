## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Deprecated
import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  @Deprecated("legacy state")
  public class Legacy(
    @ParentOptional
    public val `value`: String,
  ) : Source

  public object Empty : Source
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
// file: ParentOptional__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Source.Legacy.value])
 * 
 * Nullable accessor on [Source] exposing [Source.Legacy.value].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val value: String? = state.value
 * ```
 * 
 * 
 * @see Source
 * @see Source.Legacy.value
 */
@Deprecated("legacy state")
@Suppress("REDUNDANT_ELSE_IN_WHEN", "DEPRECATION", "DEPRECATION_ERROR")
public val me.tbsten.cream.generated.Source.value: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Legacy -> value
        else -> null
    }
````
