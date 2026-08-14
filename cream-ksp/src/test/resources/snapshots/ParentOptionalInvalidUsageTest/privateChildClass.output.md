## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:10: Invalid cream usage: @ParentOptional property diag.po.Hidden.secret is declared by a class the generated accessor cannot reference: diag.po.Hidden is private, so the generated `is` check would not compile.

Solution: 
  Make diag.po.Hidden public or internal, or remove @ParentOptional from diag.po.Hidden.secret.
```

## Input

```kt
package diag.po

import me.tbsten.cream.ParentOptional

sealed interface MyState {
    data object Loading : MyState
}

private data class Hidden(
    @ParentOptional val secret: String,
) : MyState
```
