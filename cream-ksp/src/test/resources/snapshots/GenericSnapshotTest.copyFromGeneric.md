````kt
// file: CopyFrom__Target.kt
package snap.generic

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Target])
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
public fun <T : Any?> snap.generic.Source<T>.copyToTarget(
    value: T = this.value,
    label: String = this.label,
) : snap.generic.Target<T> = snap.generic.Target(
    value = value,
    label = label,
)
````
