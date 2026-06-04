## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:7: Invalid cream usage: Unsupported target sealed class (diag.State). A sealed class cannot be instantiated directly.

Solution: 
  Specify one of its concrete subclasses as the target.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyMapping

data class Source(val id: String)

sealed class State(val id: String)

@CopyMapping(Source::class, State::class)
object Mapping
```
