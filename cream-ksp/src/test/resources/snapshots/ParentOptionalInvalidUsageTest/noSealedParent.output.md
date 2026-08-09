## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:5: Invalid cream usage: @ParentOptional property diag.po.Plain.data has no sealed parent type: its enclosing class does not (transitively) implement any sealed class/interface, so there is no receiver to generate the accessor on.

Solution: 
  Make the enclosing class part of a sealed hierarchy, or remove @ParentOptional.
```

## Input

```kt
package diag.po

import me.tbsten.cream.ParentOptional

class Plain(@ParentOptional val data: String)
```
