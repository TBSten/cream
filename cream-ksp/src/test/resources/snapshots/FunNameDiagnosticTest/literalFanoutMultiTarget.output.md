## Compiler output

```text
Invalid cream usage: @CopyTo on diag.Source sets a fixed funName "toThem",
but it generates more than one function (multiple targets or sources, a sealed
target, or a reversible mapping). Those functions would all share that name and collide.

Solution: 
  Include a naming token so each generated function gets a distinct name, e.g.
    funName = "to" + CopyTargetSimpleName
  or split the declaration into separate annotations.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyTo on diag.Source sets a fixed funName "toThem",
but it generates more than one function (multiple targets or sources, a sealed
target, or a reversible mapping). Those functions would all share that name and collide.

Solution: 
  Include a naming token so each generated function gets a distinct name, e.g.
    funName = "to" + CopyTargetSimpleName
  or split the declaration into separate annotations.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

@CopyTo(A::class, B::class, funName = "toThem")
data class Source(val shared: String)

data class A(val shared: String, val a: Int)
data class B(val shared: String, val b: Int)
```
