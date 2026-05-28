## Compiler output

```text
Invalid cream usage: Unsupported copy to interface (diag.Plain).It must be a sealed interface.

Solution: 
  Please make diag.Plain a sealed interface.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: Unsupported copy to interface (diag.Plain).It must be a sealed interface.

Solution: 
  Please make diag.Plain a sealed interface.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

interface Plain {
    val id: String
}

@CopyTo(Plain::class)
data class Source(val id: String)
```
