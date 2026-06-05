## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:6: Invalid cream usage: @CombineTo on diag.Source sets a fixed funName "toThem",
but it generates more than one function (multiple targets or sources, a sealed
target, or a reversible mapping). Those functions would all share that name and collide.

Solution: 
  Include a naming token so each generated function gets a distinct name, e.g.
    funName = "to" + CopyTargetSimpleName
  or split the declaration into separate annotations.
```

## Input

```kt
package diag

import me.tbsten.cream.CombineTo

@CombineTo(A::class, B::class, funName = "toThem")
data class Source(val shared: String)

data class A(val shared: String, val x: Int)
data class B(val shared: String, val y: Int)
```
