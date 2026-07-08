## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import me.tbsten.cream.ParentOptional

public sealed interface Source {
  public class Child : Source {
    @ParentOptional
    public val lazyValue: Int by lazy { 42 }
  }

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
 * (Auto generate by @[ParentOptional] annotation of [Source.Child.lazyValue])
 * 
 * Nullable accessor on [Source] exposing [Source.Child.lazyValue].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val lazyValue: Int? = state.lazyValue
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child.lazyValue
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.lazyValue: Int?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Child -> lazyValue
        else -> null
    }
````
