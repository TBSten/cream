## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] Invalid cream usage: Invalid option: Invalid ksp.arg["cream.defaultVisibility"] = not-a-visibility.
It must be one of INHERIT, PUBLIC, INTERNAL

Solution: 
  Set one of the following for ksp.arg:
  
    - "INHERIT"
    - "PUBLIC"
    - "INTERNAL"
```

## Input

```kt
package options.diag

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
data class Source(val shared: String)

data class Target(val shared: String, val extra: Int)
```
