## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: @CallFrom source callfrom.diag.Object produces the bridge parameter name "`object`", which collides with a parameter of callfrom.diag.handle.

Solution: 
  Rename the colliding parameter of callfrom.diag.handle, or rename callfrom.diag.Object.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Object(val value: String)

@CallFrom(Object::class)
fun handle(`object`: String, value: String) { }
```
