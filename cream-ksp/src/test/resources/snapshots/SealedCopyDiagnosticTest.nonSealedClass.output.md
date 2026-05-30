## Compiler output

```kt
Invalid cream usage: @SealedCopy must be applied to a sealed class/interface, but diag.sealedCopy.NotSealed is not sealed.

Solution: 
  Make diag.sealedCopy.NotSealed a `sealed class` or `sealed interface`.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @SealedCopy must be applied to a sealed class/interface, but diag.sealedCopy.NotSealed is not sealed.

Solution: 
  Make diag.sealedCopy.NotSealed a `sealed class` or `sealed interface`.

	<stack trace omitted>
```

## Input

```kt
package diag.sealedCopy

import me.tbsten.cream.SealedCopy

@SealedCopy
class NotSealed(val name: String)
```
