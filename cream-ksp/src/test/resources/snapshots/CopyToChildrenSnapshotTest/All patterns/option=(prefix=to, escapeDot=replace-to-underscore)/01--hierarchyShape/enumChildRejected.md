## Input:Input

```kt
package me.tbsten.cream.generated

import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source

public enum class Child : Source {
  A,
  B,
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "replace-to-underscore")
    arg("notCopyToObject", "false" /* default */)
}
```

## Output:ExitCode

```kt
COMPILATION_ERROR
```

## Output:Console

```kt
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Input.kt:8: Invalid cream usage: Unsupported target enum class (me.tbsten.cream.generated.Child). An enum entry cannot be constructed as a target.

Solution: 
  Specify a class, object, annotation class, or sealed interface as the target.
```

## Output:Generated sources

```kt

```
