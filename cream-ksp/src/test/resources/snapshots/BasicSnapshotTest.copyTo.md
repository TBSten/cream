````kt
// file: CopyTo__Source.kt
package snap.basic

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
 * 
 * @see Source
 * @see Target
 */
public fun  snap.basic.Source.copyToTarget(
    shared: String = this.shared,
    onlyOnTarget: Boolean,
) : snap.basic.Target = snap.basic.Target(
    shared = shared,
    onlyOnTarget = onlyOnTarget,
)
````
