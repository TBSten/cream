## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Deprecated
import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  public class Child1(
    @ParentOptional
    public val message: String,
  ) : Source

  public class Child2(
    @ParentOptional
    @Deprecated("second contributor deprecated")
    public val message: String,
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
 * (Auto generate by @[ParentOptional] annotation of [Source.Child1.message])
 * 
 * Nullable accessor on [Source] exposing [Source.Child1.message] / [Source.Child2.message].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val message: String? = state.message
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child1.message
 * @see Source.Child2.message
 */
@Deprecated("second contributor deprecated")
@Suppress("REDUNDANT_ELSE_IN_WHEN", "DEPRECATION", "DEPRECATION_ERROR")
public val me.tbsten.cream.generated.Source.message: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Child1 -> message
        is me.tbsten.cream.generated.Source.Child2 -> message
        else -> null
    }
````
