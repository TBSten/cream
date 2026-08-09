## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @CallFrom cannot be applied to a function with a reified type parameter: callfrom.diag.parse. The generated bridge function is not inline, so it cannot forward a reified type parameter.

Solution: 
  Apply @CallFrom to a function without `reified` type parameters.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val json: String)

@CallFrom(Args::class)
inline fun <reified T> parse(json: String): T = TODO()
```
