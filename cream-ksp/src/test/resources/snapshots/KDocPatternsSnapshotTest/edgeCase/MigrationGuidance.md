## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.migrationguidance

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * 旧形式への変換。新規コードでは copyToResultV2 を使うこと。
 * 本関数は v3.0 で削除予定。
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
public fun  snap.kdoc.migrationguidance.Source.copyToTarget(
    payload: String = this.payload,
) : snap.kdoc.migrationguidance.Target = snap.kdoc.migrationguidance.Target(
    payload = payload,
)
````

## Input

```kt
package snap.kdoc.migrationguidance

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(
        description = """
            旧形式への変換。新規コードでは copyToResultV2 を使うこと。
            本関数は v3.0 で削除予定。
        """,
    ),
)
data class Source(val payload: String)

data class Target(val payload: String)
```
