## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.ParentOptional

@ChildOptionals
public sealed interface Source {
  public data class Paid(
    @ParentOptional
    @ChildOptionals.Exclude
    public val amount: Int,
  ) : Source

  public object Unpaid : Source
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
// file: ChildOptionals__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Source.Paid.amount])
 * 
 * Nullable accessor on [Source] exposing [Source.Paid.amount].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val amount: Int? = state.amount
 * ```
 * 
 * 
 * @see Source
 * @see Source.Paid.amount
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.amount: Int?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Paid -> amount
        else -> null
    }
````
