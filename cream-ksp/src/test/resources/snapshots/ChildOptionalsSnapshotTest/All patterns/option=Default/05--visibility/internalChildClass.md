## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ChildOptionals

@ChildOptionals
public sealed interface Source {
  public data class Open(
    public val name: String,
  ) : Source
}

internal data class Hidden(
  public val `value`: String,
) : Source
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
// file: ChildOptionals__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ChildOptionals] annotation of [Source])
 * 
 * Nullable accessor on [Source] exposing [Hidden.value].
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
 * @see Hidden.value
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.value: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Hidden -> value
        else -> null
    }

/**
 * (Auto generate by @[ChildOptionals] annotation of [Source])
 * 
 * Nullable accessor on [Source] exposing [Source.Open.name].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
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
 * @see Source.Open.name
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.name: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Open -> name
        else -> null
    }
````
