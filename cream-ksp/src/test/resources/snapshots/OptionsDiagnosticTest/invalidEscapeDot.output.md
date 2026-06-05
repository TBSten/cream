## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] Invalid cream usage: Invalid option: Invalid ksp.arg["cream.escapeDot"] = not-an-escape

Solution: 
  Set one of the following for ksp.arg:
  
    - lower-camel-case
    - replace-to-underscore
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
data class Source(val prop: String)

data class Target(val prop: String)
```
