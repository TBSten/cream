## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:5: Invalid cream usage: Unsupported target diag.Restricted: its primary constructor is protected and cannot be called from generated code.

Solution: 
  Make the primary constructor of diag.Restricted public or internal.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

open class Restricted protected constructor(val id: String)

@CopyTo(Restricted::class)
data class Source(val id: String)
```
