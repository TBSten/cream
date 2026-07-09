## Input:me.tbsten.cream.generated.Source

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

  public class Frozen(
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
    arg("autoValueClassMapping", "true" /* default */)
}
```

## Output:ExitCode

```kt
COMPILATION_ERROR
```

## Output:Console

```kt
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Source.kt:7: Invalid cream usage: Cannot generate copy() for sealed type 'Source' because the following subtype(s) cannot be copied: Source.Empty, Source.Frozen

Solution: 
  Choose one of the following strategies on @SealedCopy:
    • @SealedCopy(nonCopyableStrategy = RETURN_AS_IS)
      → emits 'is X -> this' for non-copyable branches
    • @SealedCopy(nonCopyableStrategy = RETURN_NULL)
      → widens the return type to 'Source?' and emits 'is X -> null'
  
  For non-data class subtypes you can also:
    • Make the subtype a 'data class'
    • Add a 'copy(...)' member function that accepts the abstract properties
    • Or annotate that copy-shaped function with @SealedCopy.Via
```

## Output:Generated sources

```kt

```
