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
 * val target = source.copyToST(b = b)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = S(...)
 * val target = source.copyToST(b = b, property = value)
 * ```
 * 
 * 
 * @see S
 * @see S.T
 */
public fun  snap.exclude.S.copyToST(
    a: String = this.a,
    b: Int,
    c: Boolean = this.c,
) : snap.exclude.S.T = snap.exclude.S.T(
    a = a,
    b = b,
    c = c,
)
````

## Input

```kt
package snap.exclude

import me.tbsten.cream.CopyFrom

sealed interface S {
    val a: String
    val b: Int
    val c: Boolean

    @CopyFrom(S::class)
    data class T(
        val a: String,
        @CopyFrom.Exclude val b: Int,
        val c: Boolean,
    )
}
```
