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
 * # In a coroutine
 * 
 * ```kt
 * suspend fun reload(source: Source) = source.copyToTarget()
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
public fun  snap.kdoc.Source.copyToTarget(
    shared: String = this.shared,
) : snap.kdoc.Target = snap.kdoc.Target(
    shared = shared,
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
        examples = [
            """
            # In a coroutine

            ```kt
            suspend fun reload(source: Source) = source.copyToTarget()
            ```
            """,
        ],
    ),
)
data class Source(val shared: String)

data class Target(val shared: String)
````
