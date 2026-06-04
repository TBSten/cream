## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:5: Invalid cream usage: Unsupported target sealed class (diag.State). A sealed class cannot be instantiated directly.

Solution: 
  Specify one of its concrete subclasses as the target.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

sealed class State(val id: String)

@CopyTo(State::class)
data class Source(val id: String)
```
