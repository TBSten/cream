## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @CallFrom on callfrom.diag.handle lists the same source class more than once: callfrom.diag.Args. Duplicated sources would generate conflicting overloads with the same signature.

Solution: 
  Remove the duplicated classes from @CallFrom.sources.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

@CallFrom(Args::class, Args::class)
fun handle(value: String) { }
```
