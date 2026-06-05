## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:10: Invalid cream usage: @CombineFrom on diag.Target is repeated with conflicting funName values:
"buildX", "buildY"
Stacked @CombineFrom annotations are merged into a single generated function, so funName must be unambiguous.

Solution: 
  Set the same funName on every @CombineFrom of diag.Target, or set it on only one.
```

## Input

```kt
package diag

import me.tbsten.cream.CombineFrom

data class A(val a: String)
data class B(val b: Int)

@CombineFrom(A::class, funName = "buildX")
@CombineFrom(B::class, funName = "buildY")
data class Target(val a: String, val b: Int)
```
