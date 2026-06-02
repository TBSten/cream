## Generated

````kt
// file: CopyMapping__Mapping.kt
package snap.copymapping.vararg

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * LibSource -> LibTarget copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = LibSource(...)
 * val target = source.copyToLibTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = LibSource(...)
 * val target = source.copyToLibTarget(property = value)
 * ```
 * 
 * 
 * @see LibSource
 * @see LibTarget
 */
public fun  snap.copymapping.vararg.LibSource.copyToLibTarget(
    id: Int = this.id,
    vararg tags: String = this.tags,
) : snap.copymapping.vararg.LibTarget = snap.copymapping.vararg.LibTarget(
    id = id,
    tags = tags,
)
````

## Input

```kt
package snap.copymapping.vararg

import me.tbsten.cream.CopyMapping

class LibSource(val id: Int, val tags: Array<String>)

class LibTarget(val id: Int, vararg val tags: String)

@CopyMapping(LibSource::class, LibTarget::class)
private object Mapping
```
