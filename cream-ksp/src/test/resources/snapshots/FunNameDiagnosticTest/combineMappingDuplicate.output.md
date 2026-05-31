## Compiler output

```text
Invalid cream usage: @CombineMapping on diag.Mapping generates the function diag.A.combine more than once.
Stacked occurrences resolve to the same receiver and name (an identical signature), which collide.

Solution: 
  Remove the duplicate occurrence, or give the occurrences distinct funName values.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CombineMapping on diag.Mapping generates the function diag.A.combine more than once.
Stacked occurrences resolve to the same receiver and name (an identical signature), which collide.

Solution: 
  Remove the duplicate occurrence, or give the occurrences distinct funName values.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CombineMapping

data class A(val a: String)
data class B(val b: Int)
data class C(val a: String, val b: Int, val extra: Long)

@CombineMapping(sources = [A::class, B::class], target = C::class, funName = "combine")
@CombineMapping(sources = [A::class, B::class], target = C::class, funName = "combine")
object Mapping
```
