## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:6: Invalid cream usage: Cannot generate copy() for sealed type 'MyState' because it contains object subtype(s): MyState.Empty. Objects are singletons and have no .copy() to delegate to.

Solution: 
  Choose one of the following strategies on @SealedCopy:
    • @SealedCopy(nonCopyableStrategy = RETURN_AS_IS)
      → emits 'is X -> this' for non-copyable branches
    • @SealedCopy(nonCopyableStrategy = RETURN_NULL)
      → widens the return type to 'MyState?' and emits 'is X -> null'
```

## Input

```kt
package diag.sealedCopy

import me.tbsten.cream.SealedCopy

@SealedCopy
sealed interface MyState {
    val name: String

    data class Loading(override val name: String) : MyState
    data object Empty : MyState { override val name: String = "" }
}
```
