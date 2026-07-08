## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @ParentOptional accessor "diag.po.MyState.info" merges multiple properties of the same child diag.po.MyState.Success (message, detail).

Solution: 
  Give each property a distinct accessor name via @ParentOptional(propertyName = ...).
```

## Input

```kt
package diag.po

import me.tbsten.cream.ParentOptional

sealed interface MyState {
    data class Success(
        @ParentOptional(propertyName = "info") val message: String,
        @ParentOptional(propertyName = "info") val detail: String,
    ) : MyState
    data object Loading : MyState
}
```
