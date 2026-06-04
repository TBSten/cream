## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:5: Invalid cream usage: Unsupported target interface (diag.Plain). It must be a sealed interface.

Solution: 
  Please make diag.Plain a sealed interface.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

interface Plain {
    val id: String
}

@CopyTo(Plain::class)
data class Source(val id: String)
```
