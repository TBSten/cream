## Generated

````kt
// file: CopyFrom__S.T.kt
package snap.exclude

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [S.T])
 * 
 * S -> S.T copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = S(...)
 * val target = source.copyToST(label = label)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = S(...)
 * val target = source.copyToST(label = label, property = value)
 * ```
 * 
 * 
 * @see S
 * @see S.T
 */
public fun  snap.exclude.S.copyToST(
    label: String?,
) : snap.exclude.S.T = snap.exclude.S.T(
    label = label,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyFrom

sealed interface S {
    val label: String?

    @CopyFrom(S::class)
    data class T(
        @CopyFrom.Exclude val label: String?,
    )
}
```
