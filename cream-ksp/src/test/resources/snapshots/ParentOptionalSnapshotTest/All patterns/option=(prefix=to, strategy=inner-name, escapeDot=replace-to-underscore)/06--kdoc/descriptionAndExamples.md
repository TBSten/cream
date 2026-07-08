## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.KDoc
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  public data class Child(
    @ParentOptional(kdoc = KDoc(description = "Read the payload without a cast.", examples = ["# Recommended\n\nval payload = state.data"]))
    public val `data`: String,
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
 * (Auto generate by @[ParentOptional] annotation of [Source.Child.data])
 * 
 * Nullable accessor on [Source] exposing [Source.Child.data].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * Read the payload without a cast.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val data: String? = state.data
 * ```
 * 
 * # Recommended
 * 
 * val payload = state.data
 * 
 * 
 * @see Source
 * @see Source.Child.data
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.data: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Child -> data
        else -> null
    }
````
