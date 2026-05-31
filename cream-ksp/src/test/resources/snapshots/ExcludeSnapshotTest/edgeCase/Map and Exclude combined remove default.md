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
 * val target = source.copyToST(num = num)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = S(...)
 * val target = source.copyToST(num = num, property = value)
 * ```
 * 
 * 
 * @see S
 * @see S.T
 */
public fun  snap.exclude.S.copyToST(
    num: Int,
) : snap.exclude.S.T = snap.exclude.S.T(
    num = num,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyFrom

sealed interface S {
    val count: Int

    @CopyFrom(S::class)
    data class T(
        @CopyFrom.Map("count") @CopyFrom.Exclude val num: Int,
    )
}
```
