## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:9: Invalid cream usage: @CallFrom cannot be applied to a member extension function: callfrom.diag.Handler.handle. The bridge would need both the dispatch receiver and the extension receiver, which a generated top-level function cannot declare.

Solution: 
  Move callfrom.diag.Handler.handle to the top level, or make it a plain member function.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

class Handler {
    @CallFrom(Args::class)
    fun String.handle(value: String) { }
}
```
