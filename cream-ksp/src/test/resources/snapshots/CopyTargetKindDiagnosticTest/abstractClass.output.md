## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:5: Invalid cream usage: Unsupported target abstract class (diag.Base). An abstract class cannot be instantiated.

Solution: 
  Specify a concrete (non-abstract) class as the target.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

abstract class Base(val id: String)

@CopyTo(Base::class)
data class Source(val id: String)
```
