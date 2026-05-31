## Compiler output

```text
Invalid cream usage: @CopyFrom on diag.Target sets a fixed funName "fromThem",
but it generates more than one function (multiple targets or sources, a sealed
target, or a reversible mapping). Those functions would all share that name and collide.

Solution: 
  Include a naming token so each generated function gets a distinct name, e.g.
    funName = "to" + CopyTargetSimpleName
  or split the declaration into separate annotations.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyFrom on diag.Target sets a fixed funName "fromThem",
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

import me.tbsten.cream.CopyFrom

data class A(val shared: String)
data class B(val shared: String)

@CopyFrom(A::class, B::class, funName = "fromThem")
data class Target(val shared: String)
```
