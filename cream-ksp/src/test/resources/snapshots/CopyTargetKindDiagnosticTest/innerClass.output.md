## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:6: Invalid cream usage: Unsupported target inner class (diag.Outer.Inner). An inner class requires an enclosing instance and cannot be a target.

Solution: 
  Make diag.Outer.Inner a top-level or nested (non-inner) class.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

class Outer {
    inner class Inner(val id: String)
}

@CopyTo(Outer.Inner::class)
data class Source(val id: String)
```
