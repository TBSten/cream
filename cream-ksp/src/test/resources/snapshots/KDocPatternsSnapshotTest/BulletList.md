## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.bulletlist

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * 下書きから公開状態へ遷移する。
 * 
 * - publishedAt は呼び出し時に必須
 * - tags は引き継がれる
 * - reviewerComment は破棄される
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
public fun  snap.kdoc.bulletlist.Source.copyToTarget(
    tags: kotlin.collections.List<String> = this.tags,
    publishedAt: String,
) : snap.kdoc.bulletlist.Target = snap.kdoc.bulletlist.Target(
    tags = tags,
    publishedAt = publishedAt,
)
````

## Input

```kt
package snap.kdoc.bulletlist

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(
        description = """
            下書きから公開状態へ遷移する。

            - publishedAt は呼び出し時に必須
            - tags は引き継がれる
            - reviewerComment は破棄される
        """,
    ),
)
data class Source(val tags: List<String>, val reviewerComment: String?)

data class Target(val tags: List<String>, val publishedAt: String)
```
