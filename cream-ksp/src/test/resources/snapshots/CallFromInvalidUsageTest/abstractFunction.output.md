## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:9: Invalid cream usage: @CallFrom cannot be applied to an abstract function: callfrom.diag.Handler.handle.

Solution: 
  Apply @CallFrom to a concrete function.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

abstract class Handler {
    @CallFrom(Args::class)
    abstract fun handle(value: String)
}
```
