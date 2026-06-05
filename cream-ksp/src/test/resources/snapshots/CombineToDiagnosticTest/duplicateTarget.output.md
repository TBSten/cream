## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:8: Invalid cream usage: Duplicate target diag.Foo in @CombineTo of diag.Source.

Solution: 
  Remove the duplicate target from @CombineTo (list each target at most once).
```

## Input

```kt
package diag

import me.tbsten.cream.CombineTo

data class Foo(val id: String, val extra: Int)

@CombineTo(Foo::class, Foo::class)
data class Source(val id: String)
```
