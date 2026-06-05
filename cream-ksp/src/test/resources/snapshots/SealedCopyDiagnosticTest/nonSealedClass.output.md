## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:6: Invalid cream usage: @SealedCopy must be applied to a sealed class/interface, but diag.sealedCopy.NotSealed is not sealed.

Solution: 
  Make diag.sealedCopy.NotSealed a `sealed class` or `sealed interface`.
```

## Input

```kt
package diag.sealedCopy

import me.tbsten.cream.SealedCopy

@SealedCopy
class NotSealed(val name: String)
```
