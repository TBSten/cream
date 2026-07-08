## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @CallFrom on callfrom.diag.handle references callfrom.diag.Args, which is private. The generated top-level bridge function cannot reference it.

Solution: 
  Make callfrom.diag.Args `public` or `internal`, or remove @CallFrom.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

private data class Args(val value: String)

@CallFrom(Args::class)
fun handle(value: String) { }
```
