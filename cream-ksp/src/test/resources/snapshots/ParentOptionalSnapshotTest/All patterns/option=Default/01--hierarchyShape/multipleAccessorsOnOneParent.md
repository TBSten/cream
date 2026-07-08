## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  public data class First(
    @ParentOptional
    public val id: String,
  ) : Source

  public data class Second(
    @ParentOptional
    public val count: Int,
  ) : Source
}
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
// file: ParentOptional__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Source.First.id])
 * 
 * Nullable accessor on [Source] exposing [Source.First.id].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val id: String? = state.id
 * ```
 * 
 * 
 * @see Source
 * @see Source.First.id
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.id: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.First -> id
        else -> null
    }

/**
 * (Auto generate by @[ParentOptional] annotation of [Source.Second.count])
 * 
 * Nullable accessor on [Source] exposing [Source.Second.count].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val count: Int? = state.count
 * ```
 * 
 * 
 * @see Source
 * @see Source.Second.count
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.count: Int?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Second -> count
        else -> null
    }
````
