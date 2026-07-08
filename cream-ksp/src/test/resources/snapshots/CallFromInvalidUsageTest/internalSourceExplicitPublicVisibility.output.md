## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:9: Invalid cream usage: visibility = CopyVisibility.PUBLIC on @CallFrom requires a public bridge for callfrom.diag.handle, but callfrom.diag.Args is internal. A public declaration must not expose an internal type.

Solution: 
  Make callfrom.diag.Args public, or use CopyVisibility.INTERNAL / CopyVisibility.INHERIT.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom
import me.tbsten.cream.CopyVisibility

internal data class Args(val value: String)

@CallFrom(Args::class, visibility = CopyVisibility.PUBLIC)
fun handle(value: String) { }
```
