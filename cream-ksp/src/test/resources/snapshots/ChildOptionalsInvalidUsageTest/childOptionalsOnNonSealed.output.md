## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:6: Invalid cream usage: @ChildOptionals must be applied to a sealed class/interface, but diag.co.NotSealed is not sealed (classKind: CLASS).

Solution: 
  Make diag.co.NotSealed a `sealed class` or `sealed interface`.
```

## Input

```kt
package diag.co

import me.tbsten.cream.ChildOptionals

@ChildOptionals
class NotSealed(val data: String)
```
