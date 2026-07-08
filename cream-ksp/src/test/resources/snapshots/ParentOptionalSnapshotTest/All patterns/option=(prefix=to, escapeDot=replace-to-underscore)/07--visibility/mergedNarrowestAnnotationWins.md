## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  public data class Success(
    @ParentOptional(visibility = CopyVisibility.INTERNAL)
    public val message: String,
  ) : Source

  public data class Failure(
    @ParentOptional
    public val message: String,
  ) : Source
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
 * (Auto generate by @[ParentOptional] annotation of [Source.Success.message])
 * 
 * Nullable accessor on [Source] exposing [Source.Success.message] / [Source.Failure.message].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val message: String? = state.message
 * ```
 * 
 * 
 * @see Source
 * @see Source.Success.message
 * @see Source.Failure.message
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.message: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Success -> message
        is me.tbsten.cream.generated.Source.Failure -> message
        else -> null
    }
````
