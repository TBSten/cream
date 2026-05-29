## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.lossyconversion

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * 詳細から要約への変換。
 * 本文 body と添付 attachments は要約側に存在しないため破棄される。
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
public fun  snap.kdoc.lossyconversion.Source.copyToTarget(
    title: String,
) : snap.kdoc.lossyconversion.Target = snap.kdoc.lossyconversion.Target(
    title = title,
)
````

## Input

```kt
package snap.kdoc.lossyconversion

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(
        description = """
            詳細から要約への変換。
            本文 body と添付 attachments は要約側に存在しないため破棄される。
        """,
    ),
)
data class Source(val body: String, val attachments: List<String>)

data class Target(val title: String)
```
