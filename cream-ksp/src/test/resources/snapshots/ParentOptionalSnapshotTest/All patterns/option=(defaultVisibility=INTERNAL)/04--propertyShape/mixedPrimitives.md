## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  public data class Child(
    @ParentOptional
    public val name: String,
    @ParentOptional
    public val age: Int,
    @ParentOptional
    public val active: Boolean,
    @ParentOptional
    public val score: Double,
  ) : Source

  public object Empty : Source
}
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
 * (Auto generate by @[ParentOptional] annotation of [Source.Child.name])
 * 
 * Nullable accessor on [Source] exposing [Source.Child.name].
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
 * @see Source.Child.name
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.name: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Child -> name
        else -> null
    }

/**
 * (Auto generate by @[ParentOptional] annotation of [Source.Child.age])
 * 
 * Nullable accessor on [Source] exposing [Source.Child.age].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val age: Int? = state.age
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child.age
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.age: Int?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Child -> age
        else -> null
    }

/**
 * (Auto generate by @[ParentOptional] annotation of [Source.Child.active])
 * 
 * Nullable accessor on [Source] exposing [Source.Child.active].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val active: Boolean? = state.active
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child.active
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.active: Boolean?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Child -> active
        else -> null
    }

/**
 * (Auto generate by @[ParentOptional] annotation of [Source.Child.score])
 * 
 * Nullable accessor on [Source] exposing [Source.Child.score].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val score: Double? = state.score
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child.score
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.score: Double?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Child -> score
        else -> null
    }
````
