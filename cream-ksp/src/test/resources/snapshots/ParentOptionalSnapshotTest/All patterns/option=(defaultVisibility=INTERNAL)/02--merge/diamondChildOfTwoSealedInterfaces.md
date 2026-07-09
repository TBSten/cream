## Input:me.tbsten.cream.generated.Root

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.ParentOptional

public sealed interface Root

public sealed interface MidA : Root {
  @ParentOptional
  public val x: String
}

public sealed interface MidB : Root {
  @ParentOptional
  public val x: String
}

public class Leaf(
  override val x: String,
) : MidA,
    MidB
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
// file: ParentOptional__Root.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[ParentOptional] annotation of [MidA.x])
 * 
 * Nullable accessor on [Root] exposing [MidA.x] / [MidB.x].
 * 
 * Returns the property value when `this` is such a child, otherwise `null`.
 * 
 * # Example
 * 
 * ```kt
 * val state: Root = /* one of the subtypes */
 * val x: String? = state.x
 * ```
 * 
 * 
 * @see Root
 * @see MidA.x
 * @see MidB.x
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val me.tbsten.cream.generated.Root.x: String?
    get() = when (this) {
        is me.tbsten.cream.generated.MidA -> x
        is me.tbsten.cream.generated.MidB -> x
        else -> null
    }
````
