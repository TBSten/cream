## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  public object Empty : Source
}

public data class Child(
  @ParentOptional
  internal val `data`: String,
) : Source
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INTERNAL")
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
 * (Auto generate by @[ParentOptional] annotation of [Child.data])
 * 
 * Nullable accessor on [Source] exposing [Child.data].
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
 * @see Child.data
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.data: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Child -> data
        else -> null
    }
````
