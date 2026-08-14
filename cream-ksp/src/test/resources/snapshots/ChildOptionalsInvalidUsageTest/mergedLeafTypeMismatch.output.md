## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:7: Invalid cream usage: @ChildOptionals accessor "diag.co.MyState.value" merges properties with mismatched types: diag.co.MyState.Failure.value: kotlin.Int, diag.co.MyState.Success.value: kotlin.String.

Solution: 
  Align the property types, or give each property a distinct accessor name via @ParentOptional(propertyName = ...).
```

## Input

```kt
package diag.co

import me.tbsten.cream.ChildOptionals

@ChildOptionals
sealed interface MyState {
    data class Success(val value: String) : MyState
    data class Failure(val value: Int) : MyState
    data object Loading : MyState
}
```
