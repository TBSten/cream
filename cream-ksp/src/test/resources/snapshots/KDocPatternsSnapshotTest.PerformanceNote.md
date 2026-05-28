## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.performancenote

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * 毎回新インスタンスを生成する。高頻度ループ内での呼び出しは避ける。
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
public fun  snap.kdoc.performancenote.Source.copyToTarget(
    frame: Long = this.frame,
) : snap.kdoc.performancenote.Target = snap.kdoc.performancenote.Target(
    frame = frame,
)
````

## Input

```kt
package snap.kdoc.performancenote

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(description = "毎回新インスタンスを生成する。高頻度ループ内での呼び出しは避ける。"),
)
data class Source(val frame: Long)

data class Target(val frame: Long)
```
