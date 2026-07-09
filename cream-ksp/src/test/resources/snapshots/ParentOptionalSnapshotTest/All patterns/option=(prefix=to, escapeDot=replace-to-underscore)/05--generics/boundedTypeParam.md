## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Comparable
import me.tbsten.cream.ParentOptional

public sealed interface Source<T : Comparable<T>> {
  public data class Filled<E : Comparable<E>>(
    @ParentOptional
    public val item: E,
  ) : Source<E>
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
 * (Auto generate by @[ParentOptional] annotation of [Source.Filled.item])
 * 
 * Nullable accessor on [Source] exposing [Source.Filled.item].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val item: T? = state.item
 * ```
 * 
 * 
 * @see Source
 * @see Source.Filled.item
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val <T : Comparable<T>> me.tbsten.cream.generated.Source<T>.item: T?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Filled -> item
        else -> null
    }
````
