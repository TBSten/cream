## Compiler output

```text
Invalid cream usage: Unsupported copy to enum class (diag.Color).

Solution: 
  Please make diag.Color a class or object or sealed interface.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: Unsupported copy to enum class (diag.Color).

Solution: 
  Please make diag.Color a class or object or sealed interface.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

enum class Color { RED, BLUE }

@CopyTo(Color::class)
data class Source(val name: String)
```
