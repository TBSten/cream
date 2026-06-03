## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:5: Invalid cream usage: Unsupported target interface (diag.Action). It must be a sealed interface.

Solution: 
  Please make diag.Action a sealed interface.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

fun interface Action {
    fun run(): Int
}

@CopyTo(Action::class)
data class Source(val id: String)
```
