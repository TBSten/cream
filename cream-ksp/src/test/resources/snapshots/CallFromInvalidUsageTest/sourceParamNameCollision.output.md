## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @CallFrom source callfrom.diag.Args produces the bridge parameter name "args", which collides with a parameter of callfrom.diag.handle.

Solution: 
  Rename the colliding parameter of callfrom.diag.handle, or rename callfrom.diag.Args.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

@CallFrom(Args::class)
fun handle(args: String, value: String) { }
```
