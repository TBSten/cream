## Compiler output

```text
Invalid cream usage: Unsupported copy to annotation class (diag.Marker).

Solution: 
  Please make diag.Marker a class or object or sealed interface.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: Unsupported copy to annotation class (diag.Marker).

Solution: 
  Please make diag.Marker a class or object or sealed interface.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

annotation class Marker

@CopyTo(Marker::class)
data class Source(val name: String)
```
