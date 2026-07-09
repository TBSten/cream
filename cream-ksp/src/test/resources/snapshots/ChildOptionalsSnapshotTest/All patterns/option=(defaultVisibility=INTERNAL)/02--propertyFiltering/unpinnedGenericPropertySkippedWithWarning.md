## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ChildOptionals

@ChildOptionals
public sealed interface Source {
  public data class Tagged<M>(
    public val meta: M,
    public val label: String,
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
w: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Source.kt:9: @ChildOptionals skipped property me.tbsten.cream.generated.Source.Tagged.meta: its type references type parameter(s) M not pinned by the sealed parent me.tbsten.cream.generated.Source, so no accessor can be generated for it. Pin the type parameter on me.tbsten.cream.generated.Source (e.g. `Child<T> : Parent<T>`) to include it.
```

## Output:Generated sources

````kt
// file: ChildOptionals__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ChildOptionals] annotation of [Source])
 * 
 * Nullable accessor on [Source] exposing [Source.Tagged.label].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Source = /* one of the subtypes */
 * val label: String? = state.label
 * ```
 * 
 * 
 * @see Source
 * @see Source.Tagged.label
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Source.label: String?
    get() = when (this) {
        is me.tbsten.cream.generated.Source.Tagged<*> -> label
        else -> null
    }
````
