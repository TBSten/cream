## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.multiparagraph

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * フェッチ完了時に Loading から Success へ遷移させる。
 * 
 * userName / password は前状態から引き継がれるため、
 * 呼び出し側では新しく取得した data のみ渡せばよい。
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
public fun  snap.kdoc.multiparagraph.Source.copyToTarget(
    userName: String = this.userName,
    password: String = this.password,
    data: String,
) : snap.kdoc.multiparagraph.Target = snap.kdoc.multiparagraph.Target(
    userName = userName,
    password = password,
    data = data,
)
````

## Input

```kt
package snap.kdoc.multiparagraph

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(
        description = """
            フェッチ完了時に Loading から Success へ遷移させる。

            userName / password は前状態から引き継がれるため、
            呼び出し側では新しく取得した data のみ渡せばよい。
        """,
    ),
)
data class Source(val userName: String, val password: String)

data class Target(val userName: String, val password: String, val data: String)
```
