## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.ParentOptional

@ChildOptionals
public sealed interface Source {
  public val `data`: String

  public data class Success(
    @ParentOptional(propertyName = "dataOrNull")
    override val `data`: String,
  ) : Source

  public data class Failure(
    override val `data`: String,
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
 * (Auto generate by @[ParentOptional] annotation of [Source.Success.data])
 * 
 * Nullable accessor on [Source] exposing [Source.Success.data].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val dataOrNull: String? = state.dataOrNull
 * ```
 * 
 * 
 * @see Source
 * @see Source.Success.data
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.dataOrNull: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Success -> data
        else -> null
    }
````
