## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:10: Invalid cream usage: @CallFrom cannot be applied to a local function: handle.

Solution: 
  Move handle to the top level or into a class.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

fun outer() {
    class Local {
        @CallFrom(Args::class)
        fun handle(value: String) { }
    }
    Local().handle("")
}
```
