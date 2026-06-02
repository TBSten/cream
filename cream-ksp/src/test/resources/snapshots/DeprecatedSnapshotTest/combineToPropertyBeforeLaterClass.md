## Generated

````kt
// file: CombineTo__First__Target.kt
package snap.deprecated.combinetoorder

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [First])
 * 
 * [First] + [Second] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val first = First(...)
 * val second = Second(...)
 * val target = first.copyToTarget(second = Second(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val first = First(...)
 * val second = Second(...)
 * val target = first.copyToTarget(second = Second(...), property = value)
 * ```
 * 
 * 
 * @see First
 * @see Second
 * @see Target
 */
@Deprecated("first property gone")
public fun  snap.deprecated.combinetoorder.First.copyToTarget(
    second: snap.deprecated.combinetoorder.Second,
    id: Int = this.id,
    extra: String = second.extra,
) : snap.deprecated.combinetoorder.Target = snap.deprecated.combinetoorder.Target(
    id = id,
    extra = extra,
)

// ----- next file -----

// file: CombineTo__Second__Target.kt
package snap.deprecated.combinetoorder

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [Second])
 * 
 * [Second] + [First] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val second = Second(...)
 * val first = First(...)
 * val target = second.copyToTarget(first = First(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val second = Second(...)
 * val first = First(...)
 * val target = second.copyToTarget(first = First(...), property = value)
 * ```
 * 
 * 
 * @see Second
 * @see First
 * @see Target
 */
@Deprecated("second class gone")
public fun  snap.deprecated.combinetoorder.Second.copyToTarget(
    first: snap.deprecated.combinetoorder.First,
    id: Int = first.id,
    extra: String = this.extra,
) : snap.deprecated.combinetoorder.Target = snap.deprecated.combinetoorder.Target(
    id = id,
    extra = extra,
)
````

## Input

```kt
package snap.deprecated.combinetoorder

import me.tbsten.cream.CombineTo

@CombineTo(Target::class)
data class First(@Deprecated("first property gone") val id: Int)

@Deprecated("second class gone")
@CombineTo(Target::class)
data class Second(val extra: String)

data class Target(val id: Int, val extra: String)
```
