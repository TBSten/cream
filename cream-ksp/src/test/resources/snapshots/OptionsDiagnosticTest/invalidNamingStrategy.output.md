## Compiler output

```text
e: Error occurred in KSP, check log for detail
e: [ksp] Invalid cream usage: Invalid option: Invalid ksp.arg["cream.copyFunNamingStrategy"] = not-a-strategy.
It must be on of under-package, diff, simple-name, full-name, inner-name

Solution: 
  Set one of the following for ksp.arg: 
  
  
    - "under-package"
    - "diff"
    - "simple-name"
    - "full-name"
    - "inner-name"
```

## Input

```kt
package options.diag

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
data class Source(val shared: String)

data class Target(val shared: String, val extra: Int)
```
