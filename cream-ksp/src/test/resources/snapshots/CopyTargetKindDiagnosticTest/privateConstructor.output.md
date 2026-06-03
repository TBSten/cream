## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:5: Invalid cream usage: Unsupported target diag.Secret: its primary constructor is private and cannot be called from generated code.

Solution: 
  Make the primary constructor of diag.Secret public or internal.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

class Secret private constructor(val id: String)

@CopyTo(Secret::class)
data class Source(val id: String)
```
