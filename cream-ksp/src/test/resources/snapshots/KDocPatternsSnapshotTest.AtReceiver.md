## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.atreceiver

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * @receiver 承認前の申請。status は Pending である前提。
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
public fun  snap.kdoc.atreceiver.Source.copyToTarget(
    status: String = this.status,
    approvedBy: String,
) : snap.kdoc.atreceiver.Target = snap.kdoc.atreceiver.Target(
    status = status,
    approvedBy = approvedBy,
)
````

## Input

```kt
package snap.kdoc.atreceiver

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(description = "@receiver 承認前の申請。status は Pending である前提。"),
)
data class Source(val status: String)

data class Target(val status: String, val approvedBy: String)
```
