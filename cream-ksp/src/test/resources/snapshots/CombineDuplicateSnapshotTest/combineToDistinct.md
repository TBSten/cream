## Generated

````kt
// file: CombineTo__Source__Bar.kt
package snap.combineto.distinct

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [Source])
 * 
 * [Source] -> [Bar] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toBar()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toBar(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Bar
 */
public fun  snap.combineto.distinct.Source.toBar(
    id: String = this.id,
) : snap.combineto.distinct.Bar = snap.combineto.distinct.Bar(
    id = id,
)

// ----- next file -----

// file: CombineTo__Source__Foo.kt
package snap.combineto.distinct

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [Source])
 * 
 * [Source] -> [Foo] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toFoo()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.toFoo(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Foo
 */
public fun  snap.combineto.distinct.Source.toFoo(
    id: String = this.id,
) : snap.combineto.distinct.Foo = snap.combineto.distinct.Foo(
    id = id,
)
````

## Input

```kt
package snap.combineto.distinct

import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyTargetSimpleName

data class Foo(val id: String)

data class Bar(val id: String)

@CombineTo(Foo::class, Bar::class, funName = "to" + CopyTargetSimpleName)
data class Source(val id: String)
```
