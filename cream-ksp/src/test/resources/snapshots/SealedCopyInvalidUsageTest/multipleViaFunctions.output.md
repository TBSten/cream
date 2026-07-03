## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:9: Subtype 'State.Custom' declares more than one @SealedCopy.Via function (cloneA, cloneB). Mark exactly one function with @SealedCopy.Via.
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
        fun cloneA(id: String): Custom = Custom(id = id)

        @SealedCopy.Via
        fun cloneB(id: String): Custom = Custom(id = id)
    }
}
```
