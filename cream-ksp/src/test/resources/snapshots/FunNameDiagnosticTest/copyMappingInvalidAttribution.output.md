## Compiler output

```text
Invalid cream usage: @CopyMapping on diag.Mapping produced an invalid function name "to-y".
  funName template : "to-y"
  target           : diag.LibY

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyMapping on diag.Mapping produced an invalid function name "to-y".
  funName template : "to-y"
  target           : diag.LibY

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyMapping

data class LibX(val shared: String)
data class LibY(val shared: String, val extra: Int)

@CopyMapping(LibX::class, LibY::class, funName = "to-y")
object Mapping
```
