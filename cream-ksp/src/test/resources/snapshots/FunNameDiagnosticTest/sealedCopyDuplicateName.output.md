## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @SealedCopy on diag.State generates more than one function named "toState".
Stacked @SealedCopy annotations are written to one file, so each must produce a distinct name.

Solution: 
  Give each @SealedCopy a distinct funName, e.g. funName = "copyOrNull".
```

## Input

```kt
package diag

import me.tbsten.cream.SealedCopy
import me.tbsten.cream.CopyTargetSimpleName

@SealedCopy(funName = "to" + CopyTargetSimpleName)
@SealedCopy(funName = "to" + CopyTargetSimpleName)
sealed interface State {
    val id: String

    data class Loading(override val id: String) : State
    data class Loaded(override val id: String, val payload: Int) : State
}
```
