## Input:me.tbsten.cream.generated.Root

```kt
package me.tbsten.cream.generated

import me.tbsten.cream.ParentOptional

public sealed interface Root<T>

public sealed interface Middle<E> : Root<E>

public data class Leaf<X>(
  @ParentOptional
  public val item: X,
) : Middle<X>
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
COMPILATION_ERROR
```

## Output:Console

```kt
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Root.kt:11: Invalid cream usage: @ParentOptional property me.tbsten.cream.generated.Leaf.item references type parameter(s) X that are not pinned by the sealed parent me.tbsten.cream.generated.Root, so its type cannot be expressed on the parent receiver.

Solution: 
  Remove the annotation from this property, or pin the type parameter on me.tbsten.cream.generated.Root (e.g. `Child<T> : Parent<T>`).
```

## Output:Generated sources

````kt
// file: ParentOptional__Middle.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [Leaf.item])
 * 
 * Nullable accessor on [Middle] exposing [Leaf.item].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Middle = /* one of the subtypes */
 * val item: E? = state.item
 * ```
 * 
 * 
 * @see Middle
 * @see Leaf.item
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val <E : Any?> me.tbsten.cream.generated.Middle<E>.item: E?
    get() = when (this) {
        is me.tbsten.cream.generated.Leaf -> item
        else -> null
    }
````
