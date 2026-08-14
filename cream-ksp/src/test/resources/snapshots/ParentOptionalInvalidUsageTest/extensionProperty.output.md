## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @ParentOptional cannot be applied to extension property diag.po.MyState.Success.ext: the generated accessor reads the property on the child instance alone and cannot supply the extension receiver.

Solution: 
  Remove @ParentOptional from diag.po.MyState.Success.ext, or convert it to a member property.
```

## Input

```kt
package diag.po

import me.tbsten.cream.ParentOptional

sealed interface MyState {
    class Success : MyState {
        @ParentOptional
        val String.ext: Int get() = length
    }
    data object Loading : MyState
}
```
