## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:9: Invalid cream usage: @CallFrom cannot be applied to a function deprecated with level ERROR: callfrom.diag.handle. A call to it does not compile, so the generated bridge could not delegate to it.

Solution: 
  Lower the deprecation of callfrom.diag.handle to DeprecationLevel.WARNING (cream propagates it onto the bridge), or remove @CallFrom.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

@Deprecated("Use handleV2 instead.", level = DeprecationLevel.ERROR)
@CallFrom(Args::class)
fun handle(value: String) { }
```
