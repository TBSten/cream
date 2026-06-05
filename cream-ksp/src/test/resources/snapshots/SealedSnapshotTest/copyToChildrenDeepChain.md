## Generated

````kt
// file: CopyToChildren__Root.kt
package snap.sealed.deepChain

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [Root])
 * 
 * Root -> Root.Mid.Inner.Leaf copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Root(...)
 * val target = source.copyToRootMidInnerLeaf(payload = payload)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Root(...)
 * val target = source.copyToRootMidInnerLeaf(payload = payload, property = value)
 * ```
 * 
 * 
 * @see Root
 * @see Root.Mid.Inner.Leaf
 */
public fun  snap.sealed.deepChain.Root.copyToRootMidInnerLeaf(
    id: String = this.id,
    payload: Int,
) : snap.sealed.deepChain.Root.Mid.Inner.Leaf = snap.sealed.deepChain.Root.Mid.Inner.Leaf(
    id = id,
    payload = payload,
)
````

## Input

```kt
package snap.sealed.deepChain

import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface Root {
    val id: String

    sealed class Mid : Root {
        sealed class Inner : Mid() {
            data class Leaf(override val id: String, val payload: Int) : Inner()
        }
    }
}
```
