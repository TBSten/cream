## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] Invalid cream usage: Invalid option: Invalid ksp.arg["cream.nonCopyableStrategy"] = not-a-strategy.
It must be one of INHERIT, ERROR, RETURN_AS_IS, RETURN_NULL

Solution: 
  Set one of the following for ksp.arg:
  
    - "INHERIT"
    - "ERROR"
    - "RETURN_AS_IS"
    - "RETURN_NULL"
```

## Input

```kt
package options.diag

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
data class Source(val shared: String)

data class Target(val shared: String, val extra: Int)
```
