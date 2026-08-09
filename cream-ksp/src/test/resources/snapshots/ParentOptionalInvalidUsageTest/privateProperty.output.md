## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @ParentOptional property diag.po.MyState.Success.secret must be public or internal: the generated top-level accessor cannot read a private property.

Solution: 
  Make diag.po.MyState.Success.secret public or internal, or remove @ParentOptional from it.
```

## Input

```kt
package diag.po

import me.tbsten.cream.ParentOptional

sealed interface MyState {
    class Success(data: String) : MyState {
        @ParentOptional
        private val secret: String = data
    }
    data object Loading : MyState
}
```
