## Generated

````kt
// file: CopyFrom__DomainModel.kt
package snap.kdoc.inlinesymbollink

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [DomainModel])
 * 
 * Entity -> DomainModel copy function.
 * 
 * 永続化層の [Entity] からドメインモデルへ変換する。
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Entity(...)
 * val target = source.copyToDomainModel()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Entity(...)
 * val target = source.copyToDomainModel(property = value)
 * ```
 * 
 * 
 * @see Entity
 * @see DomainModel
 */
public fun  snap.kdoc.inlinesymbollink.Entity.copyToDomainModel(
    id: String = this.id,
) : snap.kdoc.inlinesymbollink.DomainModel = snap.kdoc.inlinesymbollink.DomainModel(
    id = id,
)
````

## Input

```kt
package snap.kdoc.inlinesymbollink

import me.tbsten.cream.CopyFrom
import me.tbsten.cream.KDoc

data class Entity(val id: String)

@CopyFrom(
    Entity::class,
    kdoc = KDoc(description = "永続化層の [Entity] からドメインモデルへ変換する。"),
)
data class DomainModel(val id: String)
```
