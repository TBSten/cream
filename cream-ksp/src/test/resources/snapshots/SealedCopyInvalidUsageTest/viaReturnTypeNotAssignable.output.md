## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:13: @SealedCopy.Via function 'cloneWith' returns 'String', which is not assignable to 'State.Custom'. Change its return type to 'State.Custom' (or a subtype of it).
```

## Input

```kt
package sealed.diag

import me.tbsten.cream.SealedCopy

@SealedCopy
sealed interface State {
    val id: String

    class Custom(
        override val id: String,
    ) : State {
        @SealedCopy.Via
        fun cloneWith(id: String): String = id
    }
}
```
