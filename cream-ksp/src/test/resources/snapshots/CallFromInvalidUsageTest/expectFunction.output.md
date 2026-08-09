## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @CallFrom cannot be applied to an expect function: callfrom.diag.handle. KSP processes each platform compilation separately, so the generated bridge cannot be matched with the actual declarations.

Solution: 
  Apply @CallFrom to a regular (non-expect) function in common code, or to the actual platform functions.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

@CallFrom(Args::class)
expect fun handle(value: String)
```
