## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @ParentOptional accessor "diag.po.MyState.data" is forced public (via visibility = CopyVisibility.PUBLIC on @ParentOptional) but its signature would expose the internal declaration diag.po.MyState, which Kotlin rejects ('public' member exposes its 'internal' type).

Solution: 
  Use CopyVisibility.INTERNAL (or INHERIT) for this accessor, or make diag.po.MyState public.
```

## Input

```kt
package diag.po

import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ParentOptional

internal sealed interface MyState {
    data class Success(
        @ParentOptional(visibility = CopyVisibility.PUBLIC) val data: String,
    ) : MyState
    data object Loading : MyState
}
```
