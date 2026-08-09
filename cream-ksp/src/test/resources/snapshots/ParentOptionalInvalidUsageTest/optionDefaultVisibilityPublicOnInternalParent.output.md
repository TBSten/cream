## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:6: Invalid cream usage: @ParentOptional accessor "diag.po.MyState.data" is forced public (via the cream.defaultVisibility=PUBLIC option) but its signature would expose the internal declaration diag.po.MyState, which Kotlin rejects ('public' member exposes its 'internal' type).

Solution: 
  Use CopyVisibility.INTERNAL (or INHERIT) for this accessor, or make diag.po.MyState public.
```

## Input

```kt
package diag.po

import me.tbsten.cream.ParentOptional

internal sealed interface MyState {
    data class Success(@ParentOptional val data: String) : MyState
    data object Loading : MyState
}
```
