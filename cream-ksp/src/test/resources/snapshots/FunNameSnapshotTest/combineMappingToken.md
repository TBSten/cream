## Generated

````kt
// file: CombineMapping__Mapping.kt
package snap.funname

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineMapping] annotation of [Mapping])
 * 
 * [First] + [Second] -> [Combined] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val first = First(...)
 * val second = Second(...)
 * val target = first.toCombined(second = Second(...), extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val first = First(...)
 * val second = Second(...)
 * val target = first.toCombined(second = Second(...), extra = extra, property = value)
 * ```
 * 
 * 
 * @see First
 * @see Second
 * @see Combined
 */
public fun  snap.funname.First.toCombined(
    second: snap.funname.Second,
    a: String = this.a,
    b: Int = second.b,
    extra: Long,
) : snap.funname.Combined = snap.funname.Combined(
    a = a,
    b = b,
    extra = extra,
)
````

## Input

```kt
package snap.funname

import me.tbsten.cream.CombineMapping
import me.tbsten.cream.CopyTargetSimpleName

data class First(val a: String)
data class Second(val b: Int)
data class Combined(val a: String, val b: Int, val extra: Long)

@CombineMapping(
    sources = [First::class, Second::class],
    target = Combined::class,
    funName = "to" + CopyTargetSimpleName,
)
object Mapping
```
