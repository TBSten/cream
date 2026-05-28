## Compiler output

```text
Invalid cream usage: @CopyToChildren annotation must be applied to a sealed class/interface, but false

Solution: 
  

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: @CopyToChildren annotation must be applied to a sealed class/interface, but false

Solution: 
  

	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyToChildren

@CopyToChildren
class NotSealed(val prop: String)
```
