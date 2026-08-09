## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:9: Invalid cream usage: @CallFrom funName of callfrom.diag.handle embeds a CopyTarget* naming token, which @CallFrom cannot render: it has no target class.

Solution: 
  Use DefaultCopyFunctionName (the annotated function's own name) and/or plain text, e.g. funName = DefaultCopyFunctionName + "FromArgs".
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom
import me.tbsten.cream.CopyTargetSimpleName

data class Args(val value: String)

@CallFrom(Args::class, funName = "to" + CopyTargetSimpleName)
fun handle(value: String) { }
```
