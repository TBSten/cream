## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ChildOptionals

@ChildOptionals
public sealed interface Source {
  public data class Success(
    public val `value`: String,
  ) : Source

  public data class Failure(
    public val `value`: String,
  ) : Source
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
// file: ChildOptionals__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ChildOptionals] annotation of [Source])
 * 
 * Nullable accessor on [Source] exposing [Source.Failure.value] / [Source.Success.value].
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
 * @see Source.Failure.value
 * @see Source.Success.value
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.value: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Failure -> value
        is me.tbsten.cream.generated.Source.Success -> value
        else -> null
    }
````
