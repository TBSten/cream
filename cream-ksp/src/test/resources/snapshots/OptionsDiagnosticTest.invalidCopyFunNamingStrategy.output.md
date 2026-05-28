```text
Invalid cream usage: Invalid option: Invalid ksp.arg["cream.copyFunNamingStrategy"] = not-a-strategy.
It must be on of under-package, diff, simple-name, full-name, inner-name

Solution: 
  Set one of the following for ksp.arg: 
  
  
    - "under-package"
    - "diff"
    - "simple-name"
    - "full-name"
    - "inner-name"
  

me.tbsten.cream.ksp.InvalidCreamOptionException: Invalid cream usage: Invalid option: Invalid ksp.arg["cream.copyFunNamingStrategy"] = not-a-strategy.
It must be on of under-package, diff, simple-name, full-name, inner-name

Solution: 
  Set one of the following for ksp.arg: 
  
  
    - "under-package"
    - "diff"
    - "simple-name"
    - "full-name"
    - "inner-name"
  

	<stack trace omitted>
Caused by: java.lang.IllegalArgumentException: No enum constant me.tbsten.cream.ksp.options.CopyFunNamingStrategy.not-a-strategy
	<stack trace omitted>
```
