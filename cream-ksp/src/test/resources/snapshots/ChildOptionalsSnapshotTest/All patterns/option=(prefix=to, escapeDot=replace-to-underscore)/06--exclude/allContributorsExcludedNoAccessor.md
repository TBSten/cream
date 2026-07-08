## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ChildOptionals

@ChildOptionals
public sealed interface Source {
  public data class Success(
    @ChildOptionals.Exclude
    public val token: String,
    public val `data`: String,
  ) : Source

  public data class Failure(
    @ChildOptionals.Exclude
    public val token: String,
    public val error: String,
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
// file: ChildOptionals__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ChildOptionals] annotation of [Source])
 * 
 * Nullable accessor on [Source] exposing [Source.Failure.error].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val error: String? = state.error
 * ```
 * 
 * 
 * @see Source
 * @see Source.Failure.error
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.error: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Failure -> error
        else -> null
    }

/**
 * (Auto generate by @[ChildOptionals] annotation of [Source])
 * 
 * Nullable accessor on [Source] exposing [Source.Success.data].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val data: String? = state.data
 * ```
 * 
 * 
 * @see Source
 * @see Source.Success.data
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.data: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Success -> data
        else -> null
    }
````
