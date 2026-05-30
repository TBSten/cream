## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * This function should not be used when the source is in transient state.
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
 * # Prefer this
 * 
 * ```kt
 * val target = source.copyToTarget()
 * ```
 * 
 * # Avoid this
 * 
 * ```kt
 * val target = source.copyToTarget() // do not!
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.kdoc.Source.copyToTarget(
    shared: String = this.shared,
    extra: Int,
) : snap.kdoc.Target = snap.kdoc.Target(
    shared = shared,
    extra = extra,
)
````

## Input

````kt
package snap.kdoc

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(
        description = "This function should not be used when the source is in transient state.",
        examples = [
            """
            # Prefer this

            ```kt
            val target = source.copyToTarget()
            ```
            """,
            """
            # Avoid this

            ```kt
            val target = source.copyToTarget() // do not!
            ```
            """,
        ],
    ),
)
data class Source(val shared: String)

data class Target(val shared: String, val extra: Int)
````
