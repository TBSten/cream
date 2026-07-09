## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:10: Invalid cream usage: @CallFrom source callfrom.diag.Args on callfrom.diag.handle would generate a bridge with the same signature as the existing function callfrom.diag.handle. kotlinc would reject the generated declaration as a conflicting overload.

Solution: 
  Give the bridge a distinct funName, remove the existing overload callfrom.diag.handle, or remove callfrom.diag.Args from @CallFrom.sources.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

fun handle(args: Args, value: String) { }

@CallFrom(Args::class)
fun handle(value: String) { }
```
