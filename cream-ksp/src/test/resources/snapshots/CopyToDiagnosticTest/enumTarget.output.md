## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Test.kt:5: Invalid cream usage: Unsupported target enum class (diag.Color). An enum entry cannot be constructed as a target.

Solution: 
  Specify a class, object, or annotation class as the target.
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

enum class Color { RED, BLUE }

@CopyTo(Color::class)
data class Source(val name: String)
```
