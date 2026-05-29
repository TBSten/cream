## Generated

````kt
// file: CopyTo__Source.kt
package snap.kdoc.codefenceindescription

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * 使用例:
 * ```kotlin
 * val next = prev.copyToTarget(answer = "yes")
 * ```
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
public fun  snap.kdoc.codefenceindescription.Source.copyToTarget(
    shared: String = this.shared,
    answer: String,
) : snap.kdoc.codefenceindescription.Target = snap.kdoc.codefenceindescription.Target(
    shared = shared,
    answer = answer,
)
````

## Input

````kt
package snap.kdoc.codefenceindescription

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(
        description = """
            使用例:
            ```kotlin
            val next = prev.copyToTarget(answer = "yes")
            ```
        """,
    ),
)
data class Source(val shared: String)

data class Target(val shared: String, val answer: String)
````
