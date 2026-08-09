## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @CallFrom source callfrom.diag.Args on callfrom.diag.handle generates a bridge with the same signature as the bridge generated for: callfrom.diag.handle. kotlinc would reject the generated overloads as conflicting.

Solution: 
  Give them distinct funName values, distinguishable parameter lists, or different source classes; or rename one of the functions.

e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:11: Invalid cream usage: @CallFrom source callfrom.diag.Args on callfrom.diag.handle generates a bridge with the same signature as the bridge generated for: callfrom.diag.handle. kotlinc would reject the generated overloads as conflicting.

Solution: 
  Give them distinct funName values, distinguishable parameter lists, or different source classes; or rename one of the functions.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

@CallFrom(Args::class)
fun handle(first: String) { }

@CallFrom(Args::class)
fun handle(second: String) { }
```
