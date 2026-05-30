## Compiler output

```text
Invalid cream usage: @CopyTo on diag.Source produced an invalid function name "to-target".
  funName template : "to-target"
  target           : diag.Target

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyTo on diag.Source produced an invalid function name "to-target".
  funName template : "to-target"
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

@CopyTo(Target::class, funName = "to-target")
data class Source(val shared: String)

data class Target(val shared: String, val extra: Int)
```
