## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:7: Invalid cream usage: @ParentOptional accessor "diag.po.MyState.value" merges properties with mismatched types: diag.po.MyState.Success.value: kotlin.String, diag.po.MyState.Failure.value: kotlin.Int.

Solution: 
  Align the property types, or give each property a distinct accessor name via @ParentOptional(propertyName = ...).
```

## Input

```kt
package diag.po

import me.tbsten.cream.ParentOptional

sealed interface MyState {
    data class Success(@ParentOptional val value: String) : MyState
    data class Failure(@ParentOptional val value: Int) : MyState
    data object Loading : MyState
}
```
