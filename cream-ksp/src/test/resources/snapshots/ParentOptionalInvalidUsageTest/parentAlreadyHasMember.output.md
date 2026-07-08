## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:10: Invalid cream usage: @ParentOptional cannot generate accessor "status" on diag.po.MyState: a member property with the same name is already visible on diag.po.MyState, and a member always wins over the generated extension.

Solution: 
  Rename the generated accessor via @ParentOptional(propertyName = ...), or remove/rename the member property on diag.po.MyState.
```

## Input

```kt
package diag.po

import me.tbsten.cream.ParentOptional

sealed interface MyState {
    val status: String

    data class Success(
        override val status: String,
        @ParentOptional(propertyName = "status") val code: Int,
    ) : MyState

    data object Loading : MyState {
        override val status: String = ""
    }
}
```
