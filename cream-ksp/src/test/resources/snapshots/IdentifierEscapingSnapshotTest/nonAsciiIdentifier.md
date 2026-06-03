## Generated

````kt
// file: CopyTo__注文Entity.kt
package snap.escape.nonascii

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [注文Entity])
 * 
 * 注文Entity -> 注文 copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = 注文Entity(...)
 * val target = source.copyTo注文()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = 注文Entity(...)
 * val target = source.copyTo注文(property = value)
 * ```
 * 
 * 
 * @see 注文Entity
 * @see 注文
 */
public fun  snap.escape.nonascii.注文Entity.copyTo注文(
    税込金額: Int = this.税込金額,
) : snap.escape.nonascii.注文 = snap.escape.nonascii.注文(
    税込金額 = 税込金額,
)
````

## Input

```kt
package snap.escape.nonascii

import me.tbsten.cream.CopyTo

@CopyTo(注文::class)
data class 注文Entity(val 税込金額: Int)

data class 注文(val 税込金額: Int)
```
