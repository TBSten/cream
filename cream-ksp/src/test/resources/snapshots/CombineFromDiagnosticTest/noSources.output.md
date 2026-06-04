## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:6: Invalid cream usage: @CombineFrom requires at least one source class.

Solution: 
  Specify at least one source class in @CombineFrom.sources of diag.Target.
```

## Input

```kt
package diag

import me.tbsten.cream.CombineFrom

@CombineFrom()
data class Target(val a: String)
```
