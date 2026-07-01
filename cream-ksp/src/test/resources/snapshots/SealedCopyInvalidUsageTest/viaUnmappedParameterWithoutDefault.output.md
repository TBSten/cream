## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:13: @SealedCopy.Via parameter 'extra' neither matches an abstract property (by name or @SealedCopy.Map) nor has a default value, so cream cannot supply it. Rename it to an abstract property, add @SealedCopy.Map("<property>"), or give it a default.
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
        fun cloneWith(id: String, extra: Int): Custom = Custom(id = id)
    }
}
```
