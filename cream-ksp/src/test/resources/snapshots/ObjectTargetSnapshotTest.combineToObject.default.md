## Generated

````kt
// file: CombineTo__Source__Singleton.kt
package snap.objtarget

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [Source])
 * 
 * [Source] -> [Singleton] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSingleton()
 * ```
 * 
 * 
 * @see Source
 * @see Singleton
 */
public fun snap.objtarget.Source.copyToSingleton() = snap.objtarget.Singleton
````

## Input

```kt
package snap.objtarget

import me.tbsten.cream.CombineTo

@CombineTo(Singleton::class)
data class Source(val prop: String)

data object Singleton
```
