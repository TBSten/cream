## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:6: Invalid cream usage: @ParentOptional property diag.po.MyState.Success.data references type parameter(s) T that are not pinned by the sealed parent diag.po.MyState, so its type cannot be expressed on the parent receiver.

Solution: 
  Remove the annotation from this property, or pin the type parameter on diag.po.MyState (e.g. `Child<T> : Parent<T>`).
```

## Input

```kt
package diag.po

import me.tbsten.cream.ParentOptional

sealed interface MyState {
    data class Success<T>(@ParentOptional val data: T) : MyState
    data object Loading : MyState
}
```
