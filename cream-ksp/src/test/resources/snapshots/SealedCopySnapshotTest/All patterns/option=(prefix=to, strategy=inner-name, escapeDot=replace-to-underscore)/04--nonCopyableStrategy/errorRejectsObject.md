## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.SealedCopy

@SealedCopy
public sealed interface Source {
  public val name: String

  public data class Loading(
    override val name: String,
  ) : Source

  public object Empty : Source {
    override val name: String = "empty"
  }
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "inner-name")
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
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Input.kt:7: Invalid cream usage: Cannot generate copy() for sealed type 'Source' because it contains object subtype(s): Source.Empty. Objects are singletons and have no .copy() to delegate to.

Solution: 
  Choose one of the following strategies on @SealedCopy:
    • @SealedCopy(nonCopyableStrategy = RETURN_AS_IS)
      → emits 'is X -> this' for non-copyable branches
    • @SealedCopy(nonCopyableStrategy = RETURN_NULL)
      → widens the return type to 'Source?' and emits 'is X -> null'
```

## Output:Generated sources

```kt

```
