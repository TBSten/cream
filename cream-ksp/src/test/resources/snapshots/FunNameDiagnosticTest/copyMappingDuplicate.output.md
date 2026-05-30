## Compiler output

```text
Invalid cream usage: @CopyMapping on diag.Mapping generates the function diag.A.conv more than once.
Stacked occurrences resolve to the same receiver and name (an identical signature), which collide.

Solution: 
  Remove the duplicate occurrence, or give the occurrences distinct funName values.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyMapping on diag.Mapping generates the function diag.A.conv more than once.
Stacked occurrences resolve to the same receiver and name (an identical signature), which collide.

Solution: 
  Remove the duplicate occurrence, or give the occurrences distinct funName values.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyMapping

data class A(val shared: String)
data class B(val shared: String, val extra: Int)

@CopyMapping(A::class, B::class, funName = "conv")
@CopyMapping(A::class, B::class, funName = "conv")
object Mapping
```
