## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:16: @SealedCopy.Via function 'cloneWith' does not supply every abstract property of 'State.Custom'. Missing: count. Add a parameter named after each missing property (or @SealedCopy.Map("<property>")).
```

## Input

```kt
package sealed.diag

import me.tbsten.cream.SealedCopy

@SealedCopy
@SealedCopy(funName = "copyOrNull")
sealed interface State {
    val id: String
    val count: Int

    class Custom(
        override val id: String,
        override val count: Int,
    ) : State {
        @SealedCopy.Via
        fun cloneWith(id: String): Custom = Custom(id = id, count = this.count)
    }
}
```
