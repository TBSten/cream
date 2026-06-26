## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:7: Invalid cream usage: @CopyToChildren annotation must be applied to a sealed class/interface, but multi.diag.NotSealedForChildren is not sealed (classKind: CLASS).

Solution: 
  Make multi.diag.NotSealedForChildren a sealed class/interface.

e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:10: Invalid cream usage: @SealedCopy must be applied to a sealed class/interface, but multi.diag.NotSealedForSelfCopy is not sealed.

Solution: 
  Make multi.diag.NotSealedForSelfCopy a `sealed class` or `sealed interface`.
```

## Input

```kt
package multi.diag

import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.SealedCopy

@CopyToChildren
class NotSealedForChildren(val a: String)

@SealedCopy
class NotSealedForSelfCopy(val b: String)
```
