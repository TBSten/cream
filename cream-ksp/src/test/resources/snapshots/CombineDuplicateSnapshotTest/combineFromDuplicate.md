## Generated

````kt
// file: CombineFrom__A__Target.kt
package snap.combinefrom.dup

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [Target])
 * 
 * [A] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val a = A(...)
 * val target = a.copyToTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val a = A(...)
 * val target = a.copyToTarget(property = value)
 * ```
 * 
 * 
 * @see A
 * @see Target
 */
public fun  snap.combinefrom.dup.A.copyToTarget(
    a: String = this.a,
) : snap.combinefrom.dup.Target = snap.combinefrom.dup.Target(
    a = a,
)
````

## Input

```kt
package snap.combinefrom.dup

import me.tbsten.cream.CombineFrom

data class A(val a: String)

@CombineFrom(A::class)
@CombineFrom(A::class)
data class Target(val a: String)
```
