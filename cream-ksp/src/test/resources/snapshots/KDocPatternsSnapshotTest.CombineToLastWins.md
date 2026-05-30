## Generated

````kt
// file: CombineTo__ServerState__MergedState.kt
package snap.kdoc.combinetolastwins

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [ServerState])
 * 
 * [ServerState] -> [MergedState] copy function.
 * 
 * ServerState と LocalEdit を統合する。
 * 同名プロパティは後勝ち（LocalEdit 側）が優先される点に注意。
 * 
 * # Example: Basic
 * 
 * ```kt
 * val serverState = ServerState(...)
 * val target = serverState.copyToMergedState()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val serverState = ServerState(...)
 * val target = serverState.copyToMergedState(property = value)
 * ```
 * 
 * 
 * @see ServerState
 * @see MergedState
 */
public fun  snap.kdoc.combinetolastwins.ServerState.copyToMergedState(
    title: String = this.title,
) : snap.kdoc.combinetolastwins.MergedState = snap.kdoc.combinetolastwins.MergedState(
    title = title,
)
````

## Input

```kt
package snap.kdoc.combinetolastwins

import me.tbsten.cream.CombineTo
import me.tbsten.cream.KDoc

@CombineTo(
    MergedState::class,
    kdoc = KDoc(
        description = """
            ServerState と LocalEdit を統合する。
            同名プロパティは後勝ち（LocalEdit 側）が優先される点に注意。
        """,
    ),
)
data class ServerState(val title: String)

data class LocalEdit(val title: String)

data class MergedState(val title: String)
```
