## Generated

````kt
// file: CopyTo__Source.kt
package snap.kind.classToObject

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Loaded copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToLoaded()
 * ```
 * 
 * 
 * @see Source
 * @see Loaded
 */
public fun snap.kind.classToObject.Source.copyToLoaded() = snap.kind.classToObject.Loaded
````

## Input

```kt
package snap.kind.classToObject

import me.tbsten.cream.CopyTo

@CopyTo(Loaded::class)
data class Source(val id: String)

data object Loaded
```
