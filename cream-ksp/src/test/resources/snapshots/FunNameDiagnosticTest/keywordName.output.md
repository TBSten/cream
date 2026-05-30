## Compiler output

```text
Invalid cream usage: @CopyTo on diag.Source produced an invalid function name "is".
  funName template : "is"
  target           : diag.Target

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.
  Note: "is" is a Kotlin keyword; wrap it in backticks to use it as a name, e.g. funName = "`is`".

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyTo on diag.Source produced an invalid function name "is".
  funName template : "is"
  target           : diag.Target

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.
  Note: "is" is a Kotlin keyword; wrap it in backticks to use it as a name, e.g. funName = "`is`".

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

@CopyTo(Target::class, funName = "is")
data class Source(val shared: String)

data class Target(val shared: String, val extra: Int)
```
