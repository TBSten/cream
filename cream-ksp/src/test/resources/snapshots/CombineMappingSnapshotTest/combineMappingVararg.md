## Generated

````kt
// file: CombineMapping__Mapping.kt
package snap.combinemapping.vararg

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineMapping] annotation of [Mapping])
 * 
 * [LibPrimary] + [LibOther] -> [LibTarget] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val libPrimary = LibPrimary(...)
 * val libOther = LibOther(...)
 * val target = libPrimary.copyToLibTarget(libOther = LibOther(...))
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val libPrimary = LibPrimary(...)
 * val libOther = LibOther(...)
 * val target = libPrimary.copyToLibTarget(libOther = LibOther(...), property = value)
 * ```
 * 
 * 
 * @see LibPrimary
 * @see LibOther
 * @see LibTarget
 */
public fun  snap.combinemapping.vararg.LibPrimary.copyToLibTarget(
    libOther: snap.combinemapping.vararg.LibOther,
    id: Int = this.id,
    extra: String = libOther.extra,
    vararg tags: String = this.tags,
) : snap.combinemapping.vararg.LibTarget = snap.combinemapping.vararg.LibTarget(
    id = id,
    extra = extra,
    tags = tags,
)
````

## Input

```kt
package snap.combinemapping.vararg

import me.tbsten.cream.CombineMapping

class LibPrimary(val id: Int, val tags: Array<String>)

class LibOther(val extra: String)

class LibTarget(val id: Int, val extra: String, vararg val tags: String)

@CombineMapping(
    sources = [LibPrimary::class, LibOther::class],
    target = LibTarget::class,
)
private object Mapping
```
