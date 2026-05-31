## Compiler output

```text
Invalid cream usage: Invalid option: Invalid ksp.arg["cream.escapeDot"] = not-an-escape

Solution: 
  Set one of the following for ksp.arg:
  
    - lower-camel-case
    - replace-to-underscore
  

me.tbsten.cream.ksp.InvalidCreamOptionException: Invalid cream usage: Invalid option: Invalid ksp.arg["cream.escapeDot"] = not-an-escape

Solution: 
  Set one of the following for ksp.arg:
  
    - lower-camel-case
    - replace-to-underscore
  

	<stack trace omitted>
Caused by: java.lang.IllegalArgumentException: No enum constant me.tbsten.cream.ksp.options.EscapeDot.not-an-escape
	<stack trace omitted>
```

## Input

```kt
package diag

import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
data class Source(val prop: String)

data class Target(val prop: String)
```
