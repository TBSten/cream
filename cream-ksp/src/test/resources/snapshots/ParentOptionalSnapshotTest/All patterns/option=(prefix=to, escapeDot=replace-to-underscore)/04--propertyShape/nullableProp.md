## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  public data class Child(
    @ParentOptional
    public val name: String?,
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
 * (Auto generate by @[ParentOptional] annotation of [Source.Child.name])
 * 
 * Nullable accessor on [Source] exposing [Source.Child.name].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * Note: the property type is itself nullable, so `null` is ambiguous here — it can mean "the receiver is not such a child" or the property's own `null` value. Use an `is` check when the distinction matters.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val name: String? = state.name
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child.name
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.name: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Child -> name
        else -> null
    }
````
