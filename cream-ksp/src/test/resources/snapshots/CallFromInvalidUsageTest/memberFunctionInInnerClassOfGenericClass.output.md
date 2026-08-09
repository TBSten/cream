## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:10: Invalid cream usage: @CallFrom cannot be applied to a member function of a generic class: callfrom.diag.Outer.Inner.handle (not supported yet).

Solution: 
  Apply @CallFrom to a member function of a non-generic class.
```

## Input

```kt
package callfrom.diag

import me.tbsten.cream.CallFrom

data class Args(val value: String)

class Outer<T>(val item: T) {
    inner class Inner {
        @CallFrom(Args::class)
        fun handle(value: String) { }
    }
}
```
