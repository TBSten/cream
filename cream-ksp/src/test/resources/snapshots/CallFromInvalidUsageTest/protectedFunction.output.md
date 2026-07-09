## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:9: Invalid cream usage: @CallFrom cannot be applied to a private/protected function: callfrom.diag.Handler.handle. The generated bridge function is top-level and cannot call it.

Solution: 
  Make callfrom.diag.Handler.handle `public` or `internal`.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

open class Handler {
    @CallFrom(Args::class)
    protected fun handle(value: String) { }
}
```
