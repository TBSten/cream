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
 * val target = source.copyToST(a = a, b = b)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = S(...)
 * val target = source.copyToST(a = a, b = b, property = value)
 * ```
 * 
 * 
 * @see S
 * @see S.T
 */
public fun  snap.exclude.S.copyToST(
    a: String,
    b: Int,
) : snap.exclude.S.T = snap.exclude.S.T(
    a = a,
    b = b,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyFrom

sealed interface S {
    val a: String
    val b: Int

    @CopyFrom(S::class)
    data class T(
        @CopyFrom.Exclude val a: String,
        @CopyFrom.Exclude val b: Int,
    )
}
```
