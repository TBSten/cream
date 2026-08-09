## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:9: Invalid cream usage: @CallFrom cannot be applied to a member function of a generic class: callfrom.diag.Container.handle (not supported yet).

Solution: 
  Apply @CallFrom to a member function of a non-generic class.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

class Container<T>(val item: T) {
    @CallFrom(Args::class)
    fun handle(value: String) { }
}
```
