## Generated

````kt
// file: CombineFrom__First__Combined.kt
package snap.funname

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [Combined])
 * 
 * [First] + [Second] -> [Combined] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val first = First(...)
 * val second = Second(...)
 * val target = first.buildCombined(second = Second(...), extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val first = First(...)
 * val second = Second(...)
 * val target = first.buildCombined(second = Second(...), extra = extra, property = value)
 * ```
 * 
 * 
 * @see First
 * @see Second
 * @see Combined
 */
public fun  snap.funname.First.buildCombined(
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

import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CopyTargetSimpleName

data class First(val a: String)
data class Second(val b: Int)

@CombineFrom(First::class, Second::class, funName = "build" + CopyTargetSimpleName)
data class Combined(val a: String, val b: Int, val extra: Long)
```
