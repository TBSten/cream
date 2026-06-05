## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:7: Invalid cream usage: Unsupported target abstract class (diag.Base). An abstract class cannot be instantiated.

Solution: 
  Specify a concrete (non-abstract) class as the target.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyMapping

data class Source(val id: String)

abstract class Base(val id: String)

@CopyMapping(Source::class, Base::class)
object Mapping
```
