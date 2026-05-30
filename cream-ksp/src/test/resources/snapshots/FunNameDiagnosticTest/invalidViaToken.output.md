## Compiler output

```text
Invalid cream usage: @CopyTo on diag.Source produced an invalid function name "bad-Target".
  funName template : "bad-{{cream:CopyTargetSimpleName}}"
  target           : diag.Target

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyTo on diag.Source produced an invalid function name "bad-Target".
  funName template : "bad-{{cream:CopyTargetSimpleName}}"
  target           : diag.Target

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyTargetSimpleName

@CopyTo(Target::class, funName = "bad-" + CopyTargetSimpleName)
data class Source(val shared: String)

data class Target(val shared: String, val extra: Int)
```
