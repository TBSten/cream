## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.SealedCopy

@SealedCopy(funName = "snapshot")
@SealedCopy(funName = "snapshot")
public sealed interface Source {
  public val name: String

  public data class Loading(
    override val name: String,
  ) : Source
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "replace-to-underscore")
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INHERIT" /* default */)
}
```

## Output:ExitCode

```kt
COMPILATION_ERROR
```

## Output:Console

```kt
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Input.kt:8: Invalid cream usage: @SealedCopy on me.tbsten.cream.generated.Source generates more than one function named "snapshot".
Stacked @SealedCopy annotations are written to one file, so each must produce a distinct name.

Solution: 
  Give each @SealedCopy a distinct funName, e.g. funName = "copyOrNull".
```

## Output:Generated sources

```kt

```
