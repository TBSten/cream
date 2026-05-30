## Compiler output

```text
Invalid cream usage: @CopyTo on diag.Source sets a fixed funName "toState",
but it generates more than one function (multiple targets, a sealed target,
or a reversible mapping). Those functions would all share that name and collide.

Solution: 
  Include a naming token so each generated function gets a distinct name, e.g.
    funName = "to" + CopyTargetSimpleName
  or split the declaration into separate annotations.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyTo on diag.Source sets a fixed funName "toState",
but it generates more than one function (multiple targets, a sealed target,
or a reversible mapping). Those functions would all share that name and collide.

Solution: 
  Include a naming token so each generated function gets a distinct name, e.g.
    funName = "to" + CopyTargetSimpleName
  or split the declaration into separate annotations.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

@CopyTo(State::class, funName = "toState")
data class Source(val id: String)

sealed interface State {
    val id: String

    data class Loading(override val id: String) : State
    data class Loaded(override val id: String, val payload: Int) : State
}
```
