## Compiler output

```text
Invalid cream usage: @CopyToChildren annotation must be applied to a sealed class/interface, but diag.JustData is not sealed (classKind: CLASS).

Solution: 
  Make diag.JustData a sealed class/interface.

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyToChildren annotation must be applied to a sealed class/interface, but diag.JustData is not sealed (classKind: CLASS).

Solution: 
  Make diag.JustData a sealed class/interface.

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyToChildren

@CopyToChildren
data class JustData(val prop: String)
```
