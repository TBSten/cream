## Input:me.tbsten.cream.generated.ItemMapping

```kt
package me.tbsten.cream.generated

import java.time.Instant
import kotlin.Int
import kotlin.String
import kotlin.jvm.JvmInline
import me.tbsten.cream.CopyMapping

@CopyMapping(
  GetItemApiResponse.Item::class,
  Item::class,
  properties = [CopyMapping.Map(source = "imageUrl", target = "thumbnailUrl")],
)
private object ItemMapping

public data class GetItemApiResponse(
  public val item: Item,
) {
  public data class Item(
    public val itemId: String,
    public val name: String,
    public val price: Int?,
    public val description: String,
    public val imageUrl: String?,
    public val stock: Int,
    public val updatedAt: String,
  )
}

public data class Item(
  public val itemId: ItemId,
  public val name: String,
  public val price: Int?,
  public val description: String,
  public val thumbnailUrl: String?,
  public val stock: Int,
  public val updatedAt: Instant,
)

@JvmInline
public value class ItemId(
  public val `value`: String,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INHERIT" /* default */)
}
```

## Output:ExitCode

```kt
OK
```

## Output:Console

```kt

```

## Output:Generated sources

````kt
// file: CopyMapping__ItemMapping.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyMapping] annotation of [ItemMapping])
 * 
 * GetItemApiResponse.Item -> Item copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Item(...)
 * val target = source.copyToItem(itemId = itemId, updatedAt = updatedAt)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Item(...)
 * val target = source.copyToItem(itemId = itemId, updatedAt = updatedAt, property = value)
 * ```
 * 
 * 
 * @see GetItemApiResponse.Item
 * @see Item
 */
public fun  me.tbsten.cream.generated.GetItemApiResponse.Item.copyToItem(
    itemId: ItemId,
    name: String = this.name,
    price: Int? = this.price,
    description: String = this.description,
    thumbnailUrl: String? = this.imageUrl,
    stock: Int = this.stock,
    updatedAt: java.time.Instant,
) : me.tbsten.cream.generated.Item = me.tbsten.cream.generated.Item(
    itemId = itemId,
    name = name,
    price = price,
    description = description,
    thumbnailUrl = thumbnailUrl,
    stock = stock,
    updatedAt = updatedAt,
)
````
