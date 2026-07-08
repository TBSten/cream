## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  public data class Child(
    @ParentOptional
    public val `object`: String,
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
 * (Auto generate by @[ParentOptional] annotation of [Source.Child.object])
 * 
 * Nullable accessor on [Source] exposing [Source.Child.object].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val `object`: String? = state.`object`
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child.object
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
public val me.tbsten.cream.generated.Source.`object`: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Child -> `object`
        else -> null
    }
````
