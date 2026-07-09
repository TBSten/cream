## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:9: Invalid cream usage: @ParentOptional accessor "diag.po.MyState.id" merges properties with mismatched types: diag.po.MyState.Success.id: diag.po.UserId, diag.po.MyState.Failure.id: kotlin.String.

Solution: 
  Align the property types, or give each property a distinct accessor name via @ParentOptional(propertyName = ...).
```

## Input

```kt
package diag.po

import me.tbsten.cream.ParentOptional

typealias UserId = String

sealed interface MyState {
    data class Success(@ParentOptional val id: UserId) : MyState
    data class Failure(@ParentOptional val id: String) : MyState
}
```
