## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.warning

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * エラー状態からの復帰には使用しないこと。
 * その場合は retry() 経由で再フェッチすること。
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.kdoc.warning.Source.copyToTarget(
    shared: String = this.shared,
) : snap.kdoc.warning.Target = snap.kdoc.warning.Target(
    shared = shared,
)
````

## Input

```kt
package snap.kdoc.warning

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(
        description = """
            エラー状態からの復帰には使用しないこと。
            その場合は retry() 経由で再フェッチすること。
        """,
    ),
)
data class Source(val shared: String)

data class Target(val shared: String)
```
