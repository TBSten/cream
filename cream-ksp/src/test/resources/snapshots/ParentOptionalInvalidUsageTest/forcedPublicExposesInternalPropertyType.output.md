## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:13: Invalid cream usage: @ParentOptional accessor "diag.po.MyState.d" is forced public (via visibility = CopyVisibility.PUBLIC on @ParentOptional) but its signature would expose the internal declaration diag.po.Detail, which Kotlin rejects ('public' member exposes its 'internal' type).

Solution: 
  Use CopyVisibility.INTERNAL (or INHERIT) for this accessor, or make diag.po.Detail public.
```

## Input

```kt
package diag.po

import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ParentOptional

internal class Detail(val x: Int)

sealed interface MyState {
    data object Loading : MyState
}

internal data class Hidden(
    @ParentOptional(visibility = CopyVisibility.PUBLIC) val d: Detail,
) : MyState
```
