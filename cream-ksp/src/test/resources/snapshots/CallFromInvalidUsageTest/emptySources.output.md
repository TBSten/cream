## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:6: Invalid cream usage: @CallFrom on callfrom.diag.handle has no source classes, so there is nothing to generate.

Solution: 
  Specify at least one argument-holder class, e.g. @CallFrom(MyArgs::class).
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

@CallFrom
fun handle(value: String) { }
```
