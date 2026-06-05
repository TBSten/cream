## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @CombineMapping requires at least 2 source classes, but got 1.

Solution: 
  Specify at least 2 source classes in @CombineMapping.sources
```

## Input

```kt
package diag

import me.tbsten.cream.CombineMapping

data class A(val a: String)

@CombineMapping(sources = [A::class], target = Target::class)
object Mapping

data class Target(val a: String)
```
