## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.DefaultAccessorPropertyName
import me.tbsten.cream.ParentOptional

@ChildOptionals(propertyName = DefaultAccessorPropertyName + "OrNull")
public sealed interface Source {
  public data class Success(
    public val `data`: String,
    @ParentOptional(propertyName = "resultCode")
    public val code: Int,
  ) : Source

  public object Loading : Source
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "true")
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
// file: ChildOptionals__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

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
 * val dataOrNull: String? = state.dataOrNull
 * ```
 * 
 * 
 * @see Source
 * @see Source.Success.data
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.dataOrNull: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Success -> data
        else -> null
    }

/**
 * (Auto generate by @[ParentOptional] annotation of [Source.Success.code])
 * 
 * Nullable accessor on [Source] exposing [Source.Success.code].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val resultCode: Int? = state.resultCode
 * ```
 * 
 * 
 * @see Source
 * @see Source.Success.code
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.resultCode: Int?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Success -> code
        else -> null
    }
````
