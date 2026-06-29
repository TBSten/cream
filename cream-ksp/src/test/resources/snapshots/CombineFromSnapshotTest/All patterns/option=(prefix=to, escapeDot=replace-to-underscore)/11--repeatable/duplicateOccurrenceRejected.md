## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineFrom

@CombineFrom(SourceA::class)
@CombineFrom(SourceA::class)
public data class Target(
  public val propertyA: String,
  public val propertyB: Int,
)

public data class SourceA(
  public val propertyA: String,
)
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
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Target.kt:9: Invalid cream usage: @CombineFrom on me.tbsten.cream.generated.Target generates the same overload more than once: SourceA.to_Target().
Stacked @CombineFrom annotations are written to one file, so each must produce a distinct overload.

Solution: 
  Give one of the duplicate @CombineFrom a distinct funName, or change its sources.
```

## Output:Generated sources

```kt

```
