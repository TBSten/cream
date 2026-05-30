## Compiler output

```text
Invalid cream usage: @SealedCopy on diag.State produced an invalid function name "bad-name".
  funName template : "bad-name"

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @SealedCopy on diag.State produced an invalid function name "bad-name".
  funName template : "bad-name"

Solution: 
  The generated function name must be a valid Kotlin function name —
  a plain identifier, or a backtick-quoted name.
  Adjust funName, or the tokens it expands to, so it produces one.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.SealedCopy

@SealedCopy(funName = "bad-name")
sealed interface State {
    val id: String

    data class Loading(override val id: String) : State
    data class Loaded(override val id: String, val payload: Int) : State
}
```
