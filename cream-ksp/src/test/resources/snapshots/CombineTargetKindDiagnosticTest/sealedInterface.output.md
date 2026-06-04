## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:5: Invalid cream usage: Unsupported target interface (diag.State). A combine target must be directly constructable.

Solution: 
  Specify a class, annotation class, or object as the target.
```

## Input

```kt
package diag

import me.tbsten.cream.CombineTo

sealed interface State {
    val id: String
}

@CombineTo(State::class)
data class Source(val id: String)
```
