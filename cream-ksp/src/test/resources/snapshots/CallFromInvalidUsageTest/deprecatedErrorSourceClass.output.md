## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:9: Invalid cream usage: @CallFrom on callfrom.diag.handle references callfrom.diag.Args, which is deprecated with level ERROR. The generated bridge could not reference it.

Solution: 
  Lower the deprecation of callfrom.diag.Args to DeprecationLevel.WARNING, or remove @CallFrom.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

@Deprecated("Use ArgsV2 instead.", level = DeprecationLevel.ERROR)
data class Args(val value: String)

@CallFrom(Args::class)
fun handle(value: String) { }
```
