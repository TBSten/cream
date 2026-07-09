## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Deprecated
import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  public class Child(
    @ParentOptional
    @Deprecated("use new instead")
    public val old: String,
  ) : Source

  public object Empty : Source
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
// file: ParentOptional__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Source.Child.old])
 * 
 * Nullable accessor on [Source] exposing [Source.Child.old].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val old: String? = state.old
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child.old
 */
@Deprecated("use new instead")
@Suppress("REDUNDANT_ELSE_IN_WHEN", "DEPRECATION", "DEPRECATION_ERROR")
public val me.tbsten.cream.generated.Source.old: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Child -> old
        else -> null
    }
````
