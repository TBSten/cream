## Compiler output

```text
Invalid cream usage: @CopyTo on diag.Source produced an invalid function name "copyTo`Target`OrNull".
  funName template : "DefaultCopyFunctionNameOrNull"
  target           : diag.Target

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.
  Note: when cream.escapeDot=backquote, DefaultCopyFunctionName is itself a backtick-quoted name and cannot take a prefix/suffix — use a CopyTarget* token instead.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyTo on diag.Source produced an invalid function name "copyTo`Target`OrNull".
  funName template : "DefaultCopyFunctionNameOrNull"
  target           : diag.Target

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.
  Note: when cream.escapeDot=backquote, DefaultCopyFunctionName is itself a backtick-quoted name and cannot take a prefix/suffix — use a CopyTarget* token instead.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo
import me.tbsten.cream.DefaultCopyFunctionName

@CopyTo(Target::class, funName = DefaultCopyFunctionName + "OrNull")
data class Source(val shared: String)

data class Target(val shared: String, val extra: Int)
```
