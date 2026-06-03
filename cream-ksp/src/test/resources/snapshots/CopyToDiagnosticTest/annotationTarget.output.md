## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:5: Invalid cream usage: Unsupported target annotation class (diag.Marker). An annotation class cannot be used as a target.

Solution: 
  Specify a class, object, or sealed interface as the target.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

annotation class Marker

@CopyTo(Marker::class)
data class Source(val name: String)
```
