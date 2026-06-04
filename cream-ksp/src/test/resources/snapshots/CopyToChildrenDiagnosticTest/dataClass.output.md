## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:6: Invalid cream usage: @CopyToChildren annotation must be applied to a sealed class/interface, but diag.JustData is not sealed (classKind: CLASS).

Solution: 
  Make diag.JustData a sealed class/interface.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyToChildren

@CopyToChildren
data class JustData(val prop: String)
```
